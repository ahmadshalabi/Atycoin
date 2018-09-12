package com.atycoin.network.messages;

public class GetBlocksMessage extends NetworkMessage {
    private final int senderAddress;
    private final int requiredHeight;

    public GetBlocksMessage(int senderAddress, int requiredHeight) {
        this.senderAddress = senderAddress;
        this.requiredHeight = requiredHeight;
    }

    @Override
    public String makeRequest() {
        String command = "getblocks ";
        return serialize(command, this);
    }

    public int getRequiredHeight() {
        return requiredHeight;
    }

    public int getSenderAddress() {
        return senderAddress;
    }
}