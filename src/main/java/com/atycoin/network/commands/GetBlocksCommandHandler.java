package com.atycoin.network.commands;

import com.atycoin.Blockchain;
import com.atycoin.network.messages.GetBlocksMessage;
import com.atycoin.network.messages.InventoryMessage;
import com.atycoin.network.messages.NetworkMessage;
import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;

public class GetBlocksCommandHandler implements NetworkCommand {
    private BufferedWriter output;

    @Override
    public void execute(String message, int nodeAddress) {
        GetBlocksMessage remoteMessage = new Gson().fromJson(message, GetBlocksMessage.class);
        int requiredHeight = remoteMessage.getRequiredHeight();

        Blockchain blockchain = Blockchain.getInstance();
        List<String> blocks = blockchain.getBlockHashes(requiredHeight);

        NetworkMessage responseMessage = new InventoryMessage(nodeAddress, "block", blocks);

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