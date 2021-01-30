package com.atycoin.network.commands;

import com.atycoin.database.BlocksDAO;
import com.atycoin.network.KnownNodes;
import com.atycoin.network.messages.*;
import com.google.gson.Gson;

public class VersionCommandHandler extends NetworkCommand {
    @Override
    public void execute(String message, int nodeAddress) {
        int localBestHeight = BlocksDAO.getInstance().getBestHeight();

        VersionMessage remoteMessage = new Gson().fromJson(message, VersionMessage.class);
        int remoteBestHeight = remoteMessage.getBestHeight();

        NetworkMessage responseMessage = new NullMessage(nodeAddress);

        if (localBestHeight < remoteBestHeight) {
            responseMessage = new GetBlocksMessage(nodeAddress, localBestHeight);
        } else if (localBestHeight > remoteBestHeight) {
            int version = remoteMessage.getVersion();
            responseMessage = new VersionMessage(version, nodeAddress);
        }

        KnownNodes.add(remoteMessage.getSenderAddress());

        send(remoteMessage.getSenderAddress(), responseMessage);

        NetworkMessage getKnownNodesMessage = new GetAddressesMessage(nodeAddress);
        send(remoteMessage.getSenderAddress(), getKnownNodesMessage);
    }
}