package com.atycoin.network.commands;

import com.atycoin.network.messages.AddressesMessage;
import com.atycoin.network.messages.KnownNodes;
import com.google.gson.Gson;

import java.util.List;

public class AddressesCommandHandler extends NetworkCommand {
    @Override
    public void execute(String message, int nodeAddress) {
        AddressesMessage remoteMessage = new Gson().fromJson(message, AddressesMessage.class);
        List<Integer> newNodes = remoteMessage.getAddresses();

        for (int address : newNodes) {
            KnownNodes.add(address);
        }
    }
}