package com.atycoin;

import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

//Blockchain implements interaction with a DB
public class Blockchain implements Iterable<Block> {
    private static Blockchain instance = new Blockchain();

    private static Jedis dbConnection;
    private String tipOfChain;

    //TODO: Do manual commit and support rollback (BulkTransaction in DB)
    private Blockchain() {
        dbConnection = new Jedis("localhost");
    }

    public static Blockchain getInstance() {
        instance.tipOfChain = dbConnection.get("l");
        return instance;
    }

    //TODO: Check failed connection
    public void mineBlock(ArrayList<Transaction> transactions) {
        tipOfChain = dbConnection.get("l");
        Block newBlock = Block.newBlock(transactions, Util.deserializeHash(tipOfChain));

        tipOfChain = Util.serializeHash(newBlock.hash);
        String newBlockSerialized = newBlock.serializeBlock();

        //Store new block int database and update hash of last block
        dbConnection.set(tipOfChain, newBlockSerialized);
        dbConnection.set("l", tipOfChain);
    }

    //TODO: Check failed connection
    // creates a new Blockchain with genesis Block
    public void createBlockchain(String address) {
        instance.tipOfChain = dbConnection.get("l");
        if (tipOfChain == null) {
            System.out.println("No existing blockchain found. Creating a new one...\n");

            Transaction coinbaseTransaction = Transaction.newCoinbaseTransaction(address);
            Block genesisBlock = Block.newGenesisBlock(coinbaseTransaction);

            tipOfChain = Util.serializeHash(genesisBlock.hash);

            dbConnection.set(tipOfChain, genesisBlock.serializeBlock());
            dbConnection.set("l", tipOfChain);

            System.out.println("Done!");
        } else {
            System.out.println("Blockchain already exists.");
        }
    }

    //iterator return BlockchainIterator Used to iterate over blockchain blocks
    @Override
    public Iterator<Block> iterator() {
        return new BlockchainIterator(dbConnection);
    }

    // returns a list of transactions containing unspent outputs belong to publicKeyHashed
    public ArrayList<Transaction> findUnspentTransaction(byte[] publicKeyHashed) {
        ArrayList<Transaction> unspentTransactions = new ArrayList<>();
        HashMap<String, ArrayList<Integer>> spentTransactionOutputs = new HashMap<>();

        //TODO: Optimize logic in find unspentTransactions
        Blockchain blockchain = Blockchain.getInstance();
        for (Block block : blockchain) {
            for (Transaction transaction : block.transactions) {
                String transactionId = Util.bytesToHex(transaction.transactionId);

                for (TransactionOutput transactionOutput : transaction.outputs) {
                    boolean isTransactionOutputSpent = false;

                    // Was the output spent?
                    ArrayList<Integer> spentTransactionOutputIndexes = spentTransactionOutputs.get(transactionId);
                    if (spentTransactionOutputIndexes != null) {
                        for (int spentOutIndex : spentTransactionOutputIndexes) {
                            if (spentOutIndex == transaction.outputs.indexOf(transactionOutput)) {
                                isTransactionOutputSpent = true;
                                break;
                            }
                        }
                    }

                    if (isTransactionOutputSpent) {
                        continue;
                    }

                    if (transactionOutput.isLockedWithKey(publicKeyHashed)) {
                        unspentTransactions.add(transaction);
                    }
                }

                if (!transaction.isCoinbase()) {
                    for (TransactionInput transactionInput : transaction.inputs) {
                        if (transactionInput.usesKey(publicKeyHashed)) {

                            String prevTransactionId = Util.bytesToHex(transactionInput.prevTransactionId);

                            ArrayList<Integer> spentTransactionOutputIndexes = spentTransactionOutputs.get(prevTransactionId);
                            if (spentTransactionOutputIndexes == null) {
                                spentTransactionOutputIndexes = new ArrayList<>();
                                spentTransactionOutputIndexes.add(transactionInput.transactionOutputIndex);

                                spentTransactionOutputs.put(prevTransactionId, spentTransactionOutputIndexes);
                            } else {
                                spentTransactionOutputIndexes.add(transactionInput.transactionOutputIndex);
                            }
                        }
                    }
                }
            }
        }

        return unspentTransactions;
    }

    public ArrayList<TransactionOutput> findUnspentTransactionOutputs(byte[] publicKeyHashed) {
        ArrayList<TransactionOutput> unspentTransactionOutputs = new ArrayList<>();
        ArrayList<Transaction> unspentTransactions = findUnspentTransaction(publicKeyHashed);

        for (Transaction transaction : unspentTransactions) {
            for (TransactionOutput transactionOutput : transaction.outputs) {
                if (transactionOutput.isLockedWithKey(publicKeyHashed)) {
                    unspentTransactionOutputs.add(transactionOutput);
                }
            }
        }

        return unspentTransactionOutputs;
    }
}