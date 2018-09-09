package com.atycoin.network.messages;

import com.atycoin.Block;
import com.google.gson.Gson;

public class BlockMessage implements NetworkMessage {
    private final int senderAddress;
    private final Block block;

    public BlockMessage(int senderAddress, Block block) {
        this.senderAddress = senderAddress;
        this.block = block;
    }

    @Override
    public String makeRequest() {
        StringBuilder request = new StringBuilder();

        String command = "block ";
        String message = new Gson().toJson(this);

        request.append(command);
        request.append(message);

        return String.valueOf(request);
    }

    public int getSenderAddress() {
        return senderAddress;
    }

    public Block getBlock() {
        return block;
    }
}