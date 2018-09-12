package com.atycoin.network.commands;

import com.atycoin.Block;
import com.atycoin.Transaction;
import com.atycoin.database.BlocksDAO;
import com.atycoin.network.Mempool;
import com.atycoin.network.messages.*;
import com.google.gson.Gson;

//TODO: check if we actually have this block/transaction
public class GetDataCommandHandler extends NetworkCommand {
    @Override
    public void execute(String message, int nodeAddress) {
        GetDataMessage remoteMessage = new Gson().fromJson(message, GetDataMessage.class);
        String type = remoteMessage.getType();

        NetworkMessage responseMessage = new NullMessage(nodeAddress);
        if (type.equals("block")) {
            String blockHash = remoteMessage.getId();
            Block block = BlocksDAO.getInstance().getBlock(blockHash);

            responseMessage = new BlockMessage(nodeAddress, block);
        } else if (type.equals("tx")) {
            String transactionID = remoteMessage.getId();
            Transaction transaction = Mempool.getItem(transactionID);

            responseMessage = new TransactionMessage(nodeAddress, transaction);
        }

        send(remoteMessage.getSenderAddress(), responseMessage);
    }
}