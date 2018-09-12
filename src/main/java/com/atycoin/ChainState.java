package com.atycoin;

import com.atycoin.database.ChainStateDAO;

import java.util.*;

public class ChainState {
    private static ChainState instance;
    private ChainStateDAO chainStateDAO;

    private ChainState() {
        chainStateDAO = ChainStateDAO.getInstance();
    }

    public static ChainState getInstance() {
        if (instance == null) {
            instance = new ChainState();
        }
        return instance;
    }

    public void update(Block block) {
        List<Transaction> transactions = block.getTransactions();
        for (Transaction transaction : transactions) {
            // Remove inputs
            if (!transaction.isCoinbaseTransaction()) {
                List<TransactionInput> inputs = transaction.getInputs();
                for (TransactionInput input : inputs) {
                    byte[] id = input.getReferenceTransaction();
                    List<TransactionOutput> outputs = chainStateDAO.getReferenceOutputs(id);

                    if (outputs == null) {
                        continue;
                    }

                    int outputIndex = input.getOutputIndex();
                    outputs.remove(outputIndex);

                    if (outputs.size() == 0) {
                        chainStateDAO.deleteUnspentOutputs(id);
                    } else {
                        chainStateDAO.setUnspentOutputs(id, outputs);
                    }
                }
            }

            //Add outputs
            byte[] id = transaction.getId();
            chainStateDAO.setUnspentOutputs(id, transaction.getOutputs());
        }
    }

    public Map<String, List<Integer>> findUnspentOutputs(Wallet sender, int amount) {
        Map<String, List<Integer>> unspentOutputs = new HashMap<>();
        byte[] publicKeyHashed = sender.getPublicKeyHashed();

        int accumulated = 0;
        boolean isAmountReached = false;

        Set<String> transactionIDs = chainStateDAO.getAllReferenceTransaction();
        for (String transactionID : transactionIDs) {
            List<TransactionOutput> outputs = chainStateDAO.getReferenceOutputs(transactionID);
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

    // get balance of specific public key
    public int getBalance(byte[] publicKeyHashed) {
        int balance = 0;
        Set<String> transactionIDs = chainStateDAO.getAllReferenceTransaction();
        for (String transactionID : transactionIDs) {
            List<TransactionOutput> outputs = chainStateDAO.getReferenceOutputs(transactionID);
            for (TransactionOutput output : outputs) {
                if (output.isLockedWithKey(publicKeyHashed)) {
                    balance += output.getValue();
                }
            }
        }
        return balance;
    }
}