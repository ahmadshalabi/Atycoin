package com.atycoin.network.commands;

import com.atycoin.database.BlocksDAO;
import com.atycoin.network.messages.GetBlocksMessage;
import com.atycoin.network.messages.InventoryMessage;
import com.atycoin.network.messages.NetworkMessage;
import com.google.gson.Gson;

import java.util.List;

public class GetBlocksCommandHandler extends NetworkCommand {
    @Override
    public void execute(String message, int nodeAddress) {
        GetBlocksMessage remoteMessage = new Gson().fromJson(message, GetBlocksMessage.class);
        int requiredHeight = remoteMessage.getRequiredHeight();

        List<String> blocks = BlocksDAO.getInstance().getBlockHashes(requiredHeight);

        NetworkMessage responseMessage = new InventoryMessage(nodeAddress, "block", blocks);

        send(remoteMessage.getSenderAddress(), responseMessage);
    }
}