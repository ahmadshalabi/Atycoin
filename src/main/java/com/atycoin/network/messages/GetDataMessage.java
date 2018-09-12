package com.atycoin.network.messages;

public class GetDataMessage extends NetworkMessage {
    private final int senderAddress;
    private final String type;
    private final String id;

    public GetDataMessage(int senderAddress, String type, String id) {
        this.senderAddress = senderAddress;
        this.type = type;
        this.id = id;
    }

    @Override
    public String makeRequest() {
        String command = "getdata ";
        return serialize(command, this);
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