package com.atycoin.network.messages;

import com.google.gson.Gson;

import java.util.List;

public class InventoryMessage implements NetworkMessage {
    private int senderAddress;
    private String type;
    private List<String> items;

    public InventoryMessage(int senderAddress, String type, List<String> items) {
        this.senderAddress = senderAddress;
        this.type = type;
        this.items = items;
    }

    @Override
    public String makeRequest() {
        StringBuilder request = new StringBuilder();

        String command = "inventory ";
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

    public List<String> getItems() {
        return items;
    }
}