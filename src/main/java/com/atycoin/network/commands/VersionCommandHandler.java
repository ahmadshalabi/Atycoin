package com.atycoin.network.commands;

import com.atycoin.database.BlocksDAO;
import com.atycoin.network.Node;
import com.atycoin.network.messages.GetBlocksMessage;
import com.atycoin.network.messages.NetworkMessage;
import com.atycoin.network.messages.NullMessage;
import com.atycoin.network.messages.VersionMessage;
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

        if (!Node.getKnownNodes().contains(remoteMessage.getSenderAddress())) {
            Node.getKnownNodes().add(remoteMessage.getSenderAddress());
        }

        send(remoteMessage.getSenderAddress(), responseMessage);
    }
}