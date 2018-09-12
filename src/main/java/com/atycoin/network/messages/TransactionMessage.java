package com.atycoin.network.messages;

import com.atycoin.Transaction;

public class TransactionMessage extends NetworkMessage {
    private final int senderAddress;
    private final Transaction transaction;

    public TransactionMessage(int senderAddress, Transaction transaction) {
        this.senderAddress = senderAddress;
        this.transaction = transaction;
    }

    @Override
    public String makeRequest() {
        String command = "transaction ";
        return serialize(command, this);
    }

    public int getSenderAddress() {
        return senderAddress;
    }

    public Transaction getTransaction() {
        return transaction;
    }
}