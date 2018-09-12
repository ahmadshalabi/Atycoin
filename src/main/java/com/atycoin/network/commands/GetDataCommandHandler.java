package com.atycoin.network.commands;

import com.atycoin.Block;
import com.atycoin.Transaction;
import com.atycoin.database.BlocksDAO;
import com.atycoin.network.Mempool;
import com.atycoin.network.messages.*;
import com.google.gson.Gson;

public class GetDataCommandHandler extends NetworkCommand {
    @Override
    public void execute(String message, int nodeAddress) {
        GetDataMessage remoteMessage = new Gson().fromJson(message, GetDataMessage.class);
        String type = remoteMessage.getType();

        NetworkMessage responseMessage = new NullMessage(nodeAddress);
        String itemID = remoteMessage.getId();
        if (type.equals("block")) {
            Block block = BlocksDAO.getInstance().getBlock(itemID);
            responseMessage = new BlockMessage(nodeAddress, block);
        } else if (type.equals("tx")) {
            Transaction transaction = Mempool.getItem(itemID);
            responseMessage = new TransactionMessage(nodeAddress, transaction);
        }

        send(remoteMessage.getSenderAddress(), responseMessage);
    }
}