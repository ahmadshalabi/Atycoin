package com.atycoin.network.commands;

import com.atycoin.network.BlocksInTransit;
import com.atycoin.network.Mempool;
import com.atycoin.network.messages.GetDataMessage;
import com.atycoin.network.messages.InventoryMessage;
import com.atycoin.network.messages.NetworkMessage;
import com.atycoin.network.messages.NullMessage;
import com.google.gson.Gson;

import java.util.List;

public class InventoryCommandHandler extends NetworkCommand {
    @Override
    public void execute(String message, int nodeAddress) {
        InventoryMessage remoteMessage = new Gson().fromJson(message, InventoryMessage.class);
        List<String> items = remoteMessage.getItems();
        String type = remoteMessage.getType();

        System.out.printf("Received inventory with %d %s%n", items.size(), type);

        NetworkMessage responseMessage = new NullMessage(nodeAddress);
        if (type.equals("block")) {
            //track downloaded blocks
            BlocksInTransit.addItems(items);

            String blockHash = items.get(items.size() - 1);
            //TODO: Transfer blocks from different nodes
            responseMessage = new GetDataMessage(nodeAddress, "block", blockHash);

            BlocksInTransit.removeItem(items.size() - 1);
        } else if (type.equals("tx")) {
            String txId = items.get(0);

            if (Mempool.getItem(txId) == null) {
                responseMessage = new GetDataMessage(nodeAddress, "tx", txId);
            }
        }

        send(remoteMessage.getSenderAddress(), responseMessage);
    }
}