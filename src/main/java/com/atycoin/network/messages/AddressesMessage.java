package com.atycoin.network.messages;

import java.util.List;

public class AddressesMessage extends NetworkMessage {
    private List<Integer> addresses;

    public AddressesMessage(int senderAddress, List<Integer> addresses) {
        super(senderAddress);
        this.addresses = addresses;
    }

    @Override
    public String makeRequest() {
        addresses = KnownNodes.getKnownNodes();
        String command = "addresses "; //space, Indicator to find command
        return serialize(command, this);
    }

    public List<Integer> getAddresses() {
        return addresses;
    }
}