package com.atycoin.network.commands;

import com.atycoin.network.Node;
import com.atycoin.network.messages.AddressesMessage;
import com.atycoin.network.messages.GetBlocksMessage;
import com.atycoin.network.messages.NetworkMessage;
import com.google.gson.Gson;

import java.util.List;

//TODO: Implement it as in https://en.bitcoin.it/wiki/Network#Addr and https://en.bitcoin.it/wiki/Protocol_documentation#addr

public class AddressesCommandHandler extends NetworkCommand {
    @Override
    public void execute(String message, int nodeAddress) {
        AddressesMessage remoteMessage = new Gson().fromJson(message, AddressesMessage.class);
        List<Integer> knownNodes = remoteMessage.getAddresses();

        Node.getKnownNodes().addAll(knownNodes);
        System.out.printf("There are %d known nodes now!%n", Node.getKnownNodes().size());
        requestBlocks();
    }

    private void requestBlocks() {
        List<Integer> knownNodes = Node.getKnownNodes();

        for (int node : knownNodes) {
            NetworkMessage inventoryMessage = new GetBlocksMessage(node, 0);
            send(node, inventoryMessage);
        }
    }
}