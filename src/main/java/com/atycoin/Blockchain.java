package com.atycoin;

import com.atycoin.utility.Hash;
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
            addBlock(genesisBlock);

            System.out.println("Done!");
        } else {
            System.out.println("Blockchain already exists.");
        }
    }

    public Block mineBlock(List<Transaction> transactions) {
        for (int i = 0, size = transactions.size(); i < size; i++) {
            Transaction transaction = transactions.get(0);
            if (!verifyTransaction(transaction)) {
                transactions.remove(transaction);
            }
        }

        String tipOfChain = blocksDAO.getTipOfChain();
        byte[] previousBlockHash = Hash.deserialize(tipOfChain);

        int newHeight = blocksDAO.getBestHeight() + 1;

        Block newBlock = Block.newBlock(transactions, previousBlockHash, newHeight);
        addBlock(newBlock);

        return newBlock;
    }

    // signs inputs of a Transaction
    public boolean signTransaction(Transaction transaction, ECPrivateKey privateKey) {
        if (transaction.isCoinbaseTransaction()) {
            return true;
        }

        Map<String, Transaction> referenceTransactions = new HashMap<>();

        List<TransactionInput> inputs = transaction.getInputs();
        for (TransactionInput input : inputs) {
            byte[] referenceTransactionID = input.getReferenceTransaction();
            Transaction referenceTransaction = findTransaction(referenceTransactionID);
            if (referenceTransaction == null) {
                return false;
            }
            String key = Hash.serialize(referenceTransactionID);
            referenceTransactions.put(key, referenceTransaction);
        }

        transaction.sign(privateKey, referenceTransactions);
        return true;
    }

    // verifies transaction input signatures
    public boolean verifyTransaction(Transaction transaction) {
        if (transaction.isCoinbaseTransaction()) {
            return true;
        }

        Map<String, Transaction> referenceTransactions = new HashMap<>();

        List<TransactionInput> inputs = transaction.getInputs();
        for (TransactionInput input : inputs) {
            byte[] referenceTransactionID = input.getReferenceTransaction();
            Transaction referenceTransaction = findTransaction(referenceTransactionID);
            if (referenceTransaction == null) {
                System.out.println("Transaction is not Found");
                return false;
            }
            String key = Hash.serialize(referenceTransactionID);
            referenceTransactions.put(key, referenceTransaction);
        }

        return transaction.verify(referenceTransactions);
    }

    private void addBlock(Block newBlock) {
        blocksDAO.addBlock(newBlock);
        UTXOSet.getInstance().update(newBlock);
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