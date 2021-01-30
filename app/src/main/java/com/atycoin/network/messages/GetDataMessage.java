package com.atycoin.network.messages;

public class GetDataMessage extends NetworkMessage {
    private final String type;
    private final String id;

    public GetDataMessage(int senderAddress, String type, String id) {
        super(senderAddress);
        this.type = type;
        this.id = id;
    }

    @Override
    public String makeRequest() {
        String command = "getdata ";
        return serialize(command, this);
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }
}