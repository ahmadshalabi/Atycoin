package com.atycoin.network.messages;

import com.google.gson.Gson;

public class GetDataMessage implements NetworkMessage {
    private int senderAddress;
    private String type;
    private String id;

    public GetDataMessage(int senderAddress, String type, String id) {
        this.senderAddress = senderAddress;
        this.type = type;
        this.id = id;
    }

    @Override
    public String makeRequest() {
        StringBuilder request = new StringBuilder();

        String command = "getdata ";
        String message = new Gson().toJson(this);

        request.append(command);
        request.append(message);
        return String.valueOf(request);
    }

    public int getSenderAddress() {
        return senderAddress;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }
}