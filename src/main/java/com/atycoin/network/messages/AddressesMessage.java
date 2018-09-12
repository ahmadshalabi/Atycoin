package com.atycoin.network.messages;

import com.atycoin.network.Node;

import java.util.List;

public class AddressesMessage extends NetworkMessage {
    private List<Integer> addresses;

    public AddressesMessage(int senderAddress) {
        super(senderAddress);
    }

    @Override
    public String makeRequest() {
        addresses = Node.getKnownNodes();
        String command = "addresses "; //space, Indicator to find command
        return serialize(command, this);
    }

    public List<Integer> getAddresses() {
        return addresses;
    }
}