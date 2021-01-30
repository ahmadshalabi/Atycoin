package com.atycoin.network.messages;

public class NullMessage extends NetworkMessage {
    public NullMessage(int senderAddress) {
        super(senderAddress);
    }

    @Override
    public String makeRequest() {
        String command = "null "; //space, Indicator to find command
        return serialize(command, this);
    }
}