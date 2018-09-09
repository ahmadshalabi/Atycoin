package com.atycoin;

import org.bouncycastle.jce.interfaces.ECPrivateKey;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Blockchain {
    private static Blockchain instance;
    private static BlocksDAO blocksDAO;

    private Blockchain() {
        blocksDAO = BlocksDAO.getInstance();
    }

    public static Blockchain getInstance() {
        if (instance == null) {
            instance = new Blockchain();
        }
        return instance;
    }

    // creates a new Blockchain with genesis Block
    public void createBlockchain(String address) {
        String tipOfChain = blocksDAO.getTipOfChain();

        if (tipOfChain == null) {
            System.out.println("No existing blockchain found. Creating a new one...\n");

            Transaction coinbaseTransaction = Transaction.newCoinbaseTransaction(address);
            Block genesisBlock = Block.newGenesisBlock(coinbaseTransaction);

            UTXOSet utxoSet = UTXOSet.getInstance();
            utxoSet.update(genesisBlock);

            blocksDAO.addBlock(genesisBlock);

            System.out.println("Done!");
        } else {
            System.out.println("Blockchain already exists.");
        }
    }

    public Block mineBlock(List<Transaction> transactions) {
        for (int i = 0, size = transactions.size(); i < size; i++) {
            if (!verifyTransaction(transactions.get(0))) {
                System.out.println("ERROR: Invalid transactions");
                transactions.remove(transactions.get(0));
            }
        }

        String tipOfChain = blocksDAO.getTipOfChain();
        int newHeight = blocksDAO.getBestHeight() + 1;

        Block newBlock = Block.newBlock(transactions, Util.deserializeHash(tipOfChain), newHeight);
        blocksDAO.addBlock(newBlock);

        return newBlock;
    }

    // signs inputs of a Transaction
    public boolean signTransaction(Transaction transaction, ECPrivateKey privateKey) {
        Map<String, Transaction> previousTransactions = new HashMap<>();

        List<TransactionInput> transactionInputs = transaction.getInputs();
        for (TransactionInput input : transactionInputs) {
            Transaction previousTransaction = findTransaction(input.getTransactionID());
            if (previousTransaction == null) {
                System.out.println("Transaction is not Found");
                return false;
            }
            previousTransactions.put(Util.serializeHash(previousTransaction.getId()), previousTransaction);
        }

        transaction.sign(privateKey, previousTransactions);
        return true;
    }

    // verifies transaction input signatures
    public boolean verifyTransaction(Transaction transaction) {
        if (transaction.isCoinbaseTransaction()) {
            return true;
        }

        Map<String, Transaction> previousTransactions = new HashMap<>();

        List<TransactionInput> transactionInputs = transaction.getInputs();
        for (TransactionInput input : transactionInputs) {
            Transaction previousTransaction = findTransaction(input.getTransactionID());
            if (previousTransaction == null) {
                System.out.println("Transaction is not Found");
                return false;
            }
            previousTransactions.put(Util.serializeHash(previousTransaction.getId()), previousTransaction);
        }

        return transaction.verify(previousTransactions);
    }

    //TODO: Check if you can replace it using UTXOSet
    private Transaction findTransaction(byte[] id) {
        for (Block block : blocksDAO) {
            List<Transaction> transactions = block.getTransactions();
            for (Transaction transaction : transactions) {
                if (Arrays.equals(id, transaction.getId())) {
                    return transaction;
                }
            }
        }
        return null; // Transaction is not found
    }
}