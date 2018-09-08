package com.atycoin.network.commands;

import com.atycoin.network.Node;
import com.atycoin.network.messages.AddressesMessage;
import com.atycoin.network.messages.GetBlocksMessage;
import com.atycoin.network.messages.NetworkMessage;
import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

public class AddressesCommandHandler implements NetworkCommand {
    private BufferedWriter output;

    @Override
    public void execute(String message, int nodeAddress) {
        AddressesMessage remoteMessage = new Gson().fromJson(message, AddressesMessage.class);
        ArrayList<Integer> knownNodes = remoteMessage.getAddresses();

        Node.getKnownNodes().addAll(knownNodes);
        System.out.printf("There are %d known nodes now!%n", Node.getKnownNodes().size());
        requestBlocks();
    }

    public void requestBlocks() {
        ArrayList<Integer> knownNodes = Node.getKnownNodes();

        for (int node : knownNodes) {
            NetworkMessage inventoryMessage = new GetBlocksMessage(node, 0);
            try (Socket connection = new Socket(InetAddress.getLocalHost(), node)) {
                getOutputStream(connection);

                output.write(inventoryMessage.makeRequest());
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void getOutputStream(Socket connection) throws IOException {
        output = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
        output.flush();
    }
}