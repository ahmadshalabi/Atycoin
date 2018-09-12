package com.atycoin.network.messages;

public class NullMessage extends NetworkMessage {
    @Override
    public String makeRequest() {
        String command = "null "; //space, Indicator to find command
        return serialize(command, this);
    }
}