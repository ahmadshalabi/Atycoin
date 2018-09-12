package com.atycoin.network.messages;

import com.google.gson.Gson;

public abstract class NetworkMessage {
    private final static Gson encoder = new Gson();

    abstract public String makeRequest();

    public final String serialize(String command, NetworkMessage message) {
        String msg = encoder.toJson(message);
        return command + msg;
    }
}