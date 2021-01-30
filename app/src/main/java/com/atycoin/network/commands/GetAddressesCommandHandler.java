package com.atycoin.network.commands;

import com.atycoin.network.KnownNodes;
import com.atycoin.network.messages.AddressesMessage;
import com.atycoin.network.messages.GetAddressesMessage;
import com.atycoin.network.messages.NetworkMessage;
import com.google.gson.Gson;

import java.util.List;

public class GetAddressesCommandHandler extends NetworkCommand {
    @Override
    public void execute(String message, int nodeAddress) {
        GetAddressesMessage remoteMessage = new Gson().fromJson(message, GetAddressesMessage.class);
        List<Integer> knownNodes = KnownNodes.getKnownNodes();

        NetworkMessage responseMessage = new AddressesMessage(nodeAddress, knownNodes);
        System.out.printf("There are %d known nodes now!%n", KnownNodes.size());
        send(remoteMessage.getSenderAddress(), responseMessage);
    }
}