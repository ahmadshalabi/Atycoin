package com.atycoin.network;

import com.atycoin.network.commands.NetworkCommand;
import com.atycoin.network.commands.NetworkCommandFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ConnectionHandler implements Runnable {
    private final Socket receivingConnection;
    private BufferedReader input;
    private final int nodeAddress;

    public ConnectionHandler(Socket receivingConnection, int nodeAddress) {
        this.receivingConnection = receivingConnection;
        this.nodeAddress = nodeAddress;
    }

    @Override
    public void run() {
        try {
            getStreamInput();
            String request = input.readLine();
            String[] requestContent = request.split("\\s+");

            String command = requestContent[0];
            String message = requestContent[1];

            NetworkCommand networkCommand = NetworkCommandFactory.getCommand(command);
            System.out.printf("Received %s command%n", command);

            networkCommand.execute(message, nodeAddress);
        } catch (IOException e) {
            stopConnection();
            throw new RuntimeException(e);
        } finally {
            stopConnection();
        }
    }

    private void getStreamInput() throws IOException {
        input = new BufferedReader(new InputStreamReader(receivingConnection.getInputStream()));
    }

    private void stopConnection() {
        try {
            input.close();
            receivingConnection.close();
        } catch (IOException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
}