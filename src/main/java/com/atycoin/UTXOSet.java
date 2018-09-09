package com.atycoin;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import redis.clients.jedis.Jedis;

import java.lang.reflect.Type;
import java.util.*;

public class UTXOSet {
    private static UTXOSet instance;

    private Jedis dbConnection;

    private UTXOSet() {
        int nodeId = AtycoinStart.getNodeID();
        dbConnection = new Jedis("localhost", nodeId + 3379);
        dbConnection.select(1); // chainstates db
    }

    public static UTXOSet getInstance() {
        if (instance == null) {
            instance = new UTXOSet();
        }
        return instance;
    }

    //updates the UTXO set with transactions from the newly mined Block
    public void update(Block block) {
        List<Transaction> transactions = block.getTransactions();
        for (Transaction transaction : transactions) {
            // Remove inputs
            if (!transaction.isCoinbaseTransaction()) {
                List<TransactionInput> transactionInputs = transaction.getInputs();
                for (TransactionInput input : transactionInputs) {
                    String serialisedTxInputID = Util.serializeHash(input.getTransactionID());
                    String serializedOuts = dbConnection.get(serialisedTxInputID);
                    List<TransactionOutput> outs = deserializeOutputs(serializedOuts);

                    outs.remove(input.getOutputIndex());

                    if (outs.size() == 0) {
                        dbConnection.del(serialisedTxInputID);
                    } else {
                        dbConnection.set(serialisedTxInputID, serializeOutputs(outs));
                    }
                }
            }

            //Add outputs
            dbConnection.set(Util.serializeHash(transaction.getId()), serializeOutputs(transaction.getOutputs()));
        }
    }

    // finds and returns unspent outputs to reference in input
    public Map<String, List<Integer>> findSpendableOutputs(byte[] publicKeyHashed, int amount) {
        Map<String, List<Integer>> unspentOutputs = new HashMap<>();
        int accumulated = 0;

        Set<String> transactionIDs = dbConnection.keys("*");

        boolean isAmountReached = false;

        for (String transactionID : transactionIDs) {
            List<TransactionOutput> outputs = deserializeOutputs(dbConnection.get(transactionID));

            for (TransactionOutput output : outputs) {
                if (output.isLockedWithKey(publicKeyHashed) && accumulated < amount) {
                    accumulated += output.getValue();

                    List<Integer> indexes =
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
    public List<TransactionOutput> findUTXO(byte[] publicKeyHashed) {
        List<TransactionOutput> UTXOs = new ArrayList<>();

        Set<String> transactionIDs = dbConnection.keys("*");

        for (String transactionID : transactionIDs) {
            List<TransactionOutput> outputs = deserializeOutputs(dbConnection.get(transactionID));

            for (TransactionOutput output : outputs) {
                if (output.isLockedWithKey(publicKeyHashed)) {
                    UTXOs.add(output);
                }
            }
        }

        return UTXOs;
    }

    private String serializeOutputs(List<TransactionOutput> outputs) {
        Gson gson = new Gson();

        Type arrayListTypeToken = new TypeToken<List<TransactionOutput>>() {
        }.getType();

        return gson.toJson(outputs, arrayListTypeToken);
    }

    private List<TransactionOutput> deserializeOutputs(String outputSerialized) {
        Gson gson = new Gson();

        Type listTypeToken = new TypeToken<List<TransactionOutput>>() {
        }.getType();

        return gson.fromJson(outputSerialized, listTypeToken);
    }

    public List<TransactionOutput> getTxOutputs(String transactionID) {
        return deserializeOutputs(dbConnection.get(transactionID));
    }
}