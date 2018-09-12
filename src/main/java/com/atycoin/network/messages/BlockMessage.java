package com.atycoin.network.messages;

import com.atycoin.Block;

public class BlockMessage extends NetworkMessage {
    private final int senderAddress;
    private final Block block;

    public BlockMessage(int senderAddress, Block block) {
        this.senderAddress = senderAddress;
        this.block = block;
    }

    @Override
    public String makeRequest() {
        String command = "block ";
        return serialize(command, this);
    }

    public int getSenderAddress() {
        return senderAddress;
    }

    public Block getBlock() {
        return block;
    }
}