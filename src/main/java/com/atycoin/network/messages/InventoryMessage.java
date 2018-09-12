package com.atycoin.network.messages;

import java.util.List;

public class InventoryMessage extends NetworkMessage {
    private final int senderAddress;
    private final String type;
    private final List<String> items;

    public InventoryMessage(int senderAddress, String type, List<String> items) {
        this.senderAddress = senderAddress;
        this.type = type;
        this.items = items;
    }

    @Override
    public String makeRequest() {
        String command = "inventory ";
        return serialize(command, this);
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