package com.atycoin.network.commands;

import com.atycoin.BlocksDAO;
import com.atycoin.network.Node;
import com.atycoin.network.messages.GetBlocksMessage;
import com.atycoin.network.messages.NetworkMessage;
import com.atycoin.network.messages.NullMessage;
import com.atycoin.network.messages.VersionMessage;
import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

public class VersionCommandHandler implements NetworkCommand {
    private BufferedWriter output;

    @Override
    public void execute(String message, int nodeAddress) {
        int localBestHeight = BlocksDAO.getInstance().getBestHeight();

        VersionMessage remoteMessage = new Gson().fromJson(message, VersionMessage.class);
        int remoteBestHeight = remoteMessage.getBestHeight();

        NetworkMessage responseMessage = new NullMessage();
        if (localBestHeight < remoteBestHeight) {
            responseMessage = new GetBlocksMessage(nodeAddress, localBestHeight);
        } else if (localBestHeight > remoteBestHeight) {
            int version = remoteMessage.getVersion();
            responseMessage = new VersionMessage(version, nodeAddress);
        }

        if (!Node.getKnownNodes().contains(remoteMessage.getSenderAddress())) {
            Node.getKnownNodes().add(remoteMessage.getSenderAddress());
        }

        try (Socket sendingConnection = new Socket(InetAddress.getLocalHost(), remoteMessage.getSenderAddress())) {
            getOutputStream(sendingConnection);
            output.write(responseMessage.makeRequest());
            output.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void getOutputStream(Socket connection) throws IOException {
        output = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
        output.flush();
    }
}