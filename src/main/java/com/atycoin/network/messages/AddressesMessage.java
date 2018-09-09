package com.atycoin.network.messages;

import com.atycoin.network.Node;
import com.google.gson.Gson;

import java.util.List;

public class AddressesMessage implements NetworkMessage {
    private List<Integer> addresses;

    @Override
    public String makeRequest() {
        addresses = Node.getKnownNodes();
        StringBuilder request = new StringBuilder();

        String command = "addresses "; //space, Indicator to find command
        String message = new Gson().toJson(this);

        request.append(command);
        request.append(message);

        return String.valueOf(request);
    }

    public List<Integer> getAddresses() {
        return addresses;
    }
}