package com.atycoin.network.messages;

import com.atycoin.database.BlocksDAO;

public class VersionMessage extends NetworkMessage {
    private final int version;
    private final int senderAddress;
    private int bestHeight;

    public VersionMessage(int version, int senderAddress) {
        this.version = version;
        this.senderAddress = senderAddress;
    }

    @Override
    public String makeRequest() {
        bestHeight = BlocksDAO.getInstance().getBestHeight();
        String command = "version "; //space, Indicator to find command
        return serialize(command, this);
    }

    public int getBestHeight() {
        return bestHeight;
    }

    public int getVersion() {
        return version;
    }

    public int getSenderAddress() {
        return senderAddress;
    }
}