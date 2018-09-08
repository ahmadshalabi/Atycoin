package com.atycoin.network.messages;

import com.atycoin.Transaction;
import com.google.gson.Gson;

public class TransactionMessage implements NetworkMessage {
    private int senderAddress;
    private Transaction transaction;

    public TransactionMessage(int senderAddress, Transaction transaction) {
        this.senderAddress = senderAddress;
        this.transaction = transaction;
    }

    @Override
    public String makeRequest() {
        StringBuilder request = new StringBuilder();

        String command = "transaction ";
        String message = new Gson().toJson(this);

        request.append(command);
        request.append(message);

        return String.valueOf(request);
    }

    public int getSenderAddress() {
        return senderAddress;
    }

    public Transaction getTransaction() {
        return transaction;
    }
}