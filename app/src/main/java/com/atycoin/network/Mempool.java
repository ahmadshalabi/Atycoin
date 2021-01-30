package com.atycoin.network;

import com.atycoin.Transaction;
import com.atycoin.utility.Hash;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mempool {
    private static final Map<String, Transaction> mempool = new HashMap<>();

    public static Transaction getItem(String txId) {
        return mempool.get(txId);
    }

    public static void addItem(Transaction transaction) {
        String serializedID = Hash.serialize(transaction.getId());
        mempool.put(serializedID, transaction);
    }

    public static int size() {
        return mempool.size();
    }

    public static Collection<Transaction> values() {
        return mempool.values();
    }

    public static void remove(List<Transaction> transactions) {
        for (Transaction transaction : transactions) {
            mempool.remove(Hash.serialize(transaction.getId()));
        }
    }
}