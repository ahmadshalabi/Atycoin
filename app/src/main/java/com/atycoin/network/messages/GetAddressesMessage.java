package com.atycoin.network.messages;

public class GetAddressesMessage extends NetworkMessage {
    public GetAddressesMessage(int senderAddress) {
        super(senderAddress);
    }

    @Override
    public String makeRequest() {
        String command = "getaddresses ";
        return serialize(command, this);
    }
}
