package com.atycoin;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import redis.clients.jedis.Jedis;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class UTXOSet {
    private static UTXOSet instance;
    private Blockchain blockchain;
    private Jedis dbConnection;

    private UTXOSet(Blockchain blockchain) {
        int nodeId = AtycoinStart.getNodeID();
        this.blockchain = blockchain;
        dbConnection = new Jedis("localhost", nodeId + 3379);
        dbConnection.select(1); // chainstates db
    }

    public static UTXOSet getInstance() {
        if (instance == null) {
            instance = new UTXOSet(Blockchain.getInstance());
        }
        return instance;
    }

    // rebuilds the UTXO set
    public void reIndex() {
        dbConnection.flushDB(); // delete chainstates db

        HashMap<String, ArrayList<TransactionOutput>> UTXO = blockchain.findUTXO();

        for (Map.Entry<String, ArrayList<TransactionOutput>> entry : UTXO.entrySet()) {
            String outsSerialized = serializeOutputs(entry.getValue());
            dbConnection.set(entry.getKey(), outsSerialized);
        }
    }

    //updates the UTXO set with transactions from the newly mined Block
    public void update(Block block) {
        for (Transaction tx : block.transactions) {
            // Remove inputs
            if (!tx.isCoinbaseTransaction()) {
                for (TransactionInput input : tx.inputs) {
                    //ArrayList<TransactionOutput> updatedOuts = new ArrayList<>();
                    String serialisedTxInputID = Util.serializeHash(input.prevTransactionId);
                    String serializedOuts = dbConnection.get(serialisedTxInputID);
                    ArrayList<TransactionOutput> outs = deserializeOutputs(serializedOuts);

                    outs.remove(input.transactionOutputIndex);

                    if (outs.size() == 0) {
                        dbConnection.del(serialisedTxInputID);
                    } else {
                        dbConnection.set(serialisedTxInputID, serializeOutputs(outs));
                    }
                }
            }

            //Add outputs
            dbConnection.set(Util.serializeHash(tx.id), serializeOutputs(tx.outputs));
        }
    }

    //returns the number of transactions in the UTXO set
    public long countTransactions() {
        return dbConnection.dbSize();
    }

    // finds and returns unspent outputs to reference in input
    public HashMap<String, ArrayList<Integer>> findSpendableOutputs(byte[] publicKeyHashed, int amount) {
        HashMap<String, ArrayList<Integer>> unspentOutputs = new HashMap<>();
        int accumulated = 0;

        Set<String> transactionIDs = dbConnection.keys("*");

        boolean isAmountReached = false;

        for (String transactionID : transactionIDs) {
            ArrayList<TransactionOutput> outputs = deserializeOutputs(dbConnection.get(transactionID));

            for (TransactionOutput output : outputs) {
                if (output.isLockedWithKey(publicKeyHashed) && accumulated < amount) {
                    accumulated += output.value;

                    ArrayList<Integer> indexes =
                            unspentOutputs.computeIfAbsent(transactionID, k -> new ArrayList<>());

                    indexes.add(outputs.indexOf(output));
                }

                if (accumulated >= amount) {
                    isAmountReached = true;
                    break;
                }
            }

            if (isAmountReached) {
                break;
            }
        }

        return unspentOutputs;
    }

    // finds UTXO for a public key hash
    public ArrayList<TransactionOutput> findUTXO(byte[] publicKeyHashed) {
        ArrayList<TransactionOutput> UTXOs = new ArrayList<>();

        Set<String> transactionIDs = dbConnection.keys("*");

        for (String transactionID : transactionIDs) {
            ArrayList<TransactionOutput> outputs = deserializeOutputs(dbConnection.get(transactionID));

            for (TransactionOutput output : outputs) {
                if (output.isLockedWithKey(publicKeyHashed)) {
                    UTXOs.add(output);
                }
            }
        }

        return UTXOs;
    }

    public String serializeOutputs(ArrayList<TransactionOutput> outputs) {
        Gson gson = new Gson();

        Type arrayListTypeToken = new TypeToken<ArrayList<TransactionOutput>>() {
        }.getType();

        return gson.toJson(outputs, arrayListTypeToken);
    }

    public ArrayList<TransactionOutput> deserializeOutputs(String outputSerialized) {
        Gson gson = new Gson();

        Type arrayListTypeToken = new TypeToken<ArrayList<TransactionOutput>>() {
        }.getType();

        return gson.fromJson(outputSerialized, arrayListTypeToken);
    }

    public ArrayList<TransactionOutput> getTxOutputs(String transactionID) {
        return deserializeOutputs(dbConnection.get(transactionID));
    }
}