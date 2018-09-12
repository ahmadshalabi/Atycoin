package com.atycoin.network.commands;

import com.atycoin.Block;
import com.atycoin.ChainState;
import com.atycoin.database.BlocksDAO;
import com.atycoin.network.BlocksInTransit;
import com.atycoin.network.messages.BlockMessage;
import com.atycoin.network.messages.GetDataMessage;
import com.atycoin.network.messages.NetworkMessage;
import com.atycoin.network.messages.NullMessage;
import com.atycoin.utility.Hash;
import com.google.gson.Gson;

public class BlockCommandHandler extends NetworkCommand {
    @Override
    public void execute(String message, int nodeAddress) {
        BlockMessage remoteMessage = new Gson().fromJson(message, BlockMessage.class);

        Block block = remoteMessage.getBlock();
        System.out.println("Received a new block!");

        BlocksDAO blocksDAO = BlocksDAO.getInstance();
        boolean isBlockAdded = blocksDAO.addBlock(block);

        if (isBlockAdded) {
            System.out.printf("Added block %s%n", Hash.serialize(block.getHash()));
            ChainState.getInstance().update(block);
        } else {
            System.out.printf("Block %s already exist%n", Hash.serialize(block.getHash()));
        }

        NetworkMessage responseMessage = new NullMessage(nodeAddress);
        if (BlocksInTransit.size() > 0) {
            String blockHash = BlocksInTransit.getItem(BlocksInTransit.size() - 1);
            responseMessage = new GetDataMessage(nodeAddress, "block", blockHash);
            BlocksInTransit.removeItem(BlocksInTransit.size() - 1);
        }

        send(remoteMessage.getSenderAddress(), responseMessage);
    }
}