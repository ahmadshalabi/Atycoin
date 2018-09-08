package com.atycoin.network.messages;

import com.google.gson.Gson;

public class GetBlocksMessage implements NetworkMessage {
    private int senderAddress;
    private int requiredHeight;

    public GetBlocksMessage(int senderAddress, int requiredHeight) {
        this.senderAddress = senderAddress;
        this.requiredHeight = requiredHeight;
    }

    @Override
    public String makeRequest() {
        StringBuilder request = new StringBuilder();

        String command = "getblocks ";
        String message = new Gson().toJson(this);

        request.append(command);
        request.append(message);

        return String.valueOf(request);
    }

    public int getRequiredHeight() {
        return requiredHeight;
    }

    public int getSenderAddress() {
        return senderAddress;
    }
}