package com.atycoin.network.messages;

import com.atycoin.Blockchain;
import com.google.gson.Gson;

public class VersionMessage implements NetworkMessage {
    private final int version;
    private final int senderAddress;
    private int bestHeight;

    public VersionMessage(int version, int senderAddress) {
        this.version = version;
        this.senderAddress = senderAddress;
    }

    @Override
    public String makeRequest() {
        bestHeight = Blockchain.getInstance().getBestHeight();

        StringBuilder request = new StringBuilder();

        String command = "version "; //space, Indicator to find command
        String message = new Gson().toJson(this);

        request.append(command);
        request.append(message);

        return String.valueOf(request);
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