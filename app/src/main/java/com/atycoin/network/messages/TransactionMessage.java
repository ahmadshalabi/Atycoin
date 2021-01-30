package com.atycoin.network.messages;

import com.atycoin.Transaction;

public class TransactionMessage extends NetworkMessage {
    private final Transaction transaction;

    public TransactionMessage(int senderAddress, Transaction transaction) {
        super(senderAddress);
        this.transaction = transaction;
    }

    @Override
    public String makeRequest() {
        String command = "transaction ";
        return serialize(command, this);
    }

    public Transaction getTransaction() {
        return transaction;
    }
}