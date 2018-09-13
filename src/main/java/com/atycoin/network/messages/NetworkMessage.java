package com.atycoin.network.messages;

import com.google.gson.Gson;

public abstract class NetworkMessage {
    private transient final static Gson encoder = new Gson();
    private final int senderAddress;

    public NetworkMessage(int senderAddress) {
        this.senderAddress = senderAddress;
    }

    abstract public String makeRequest();

    protected final String serialize(String command, NetworkMessage message) {
        String msg = encoder.toJson(message);
        return command + msg;
    }

    public int getSenderAddress() {
        return senderAddress;
    }
}