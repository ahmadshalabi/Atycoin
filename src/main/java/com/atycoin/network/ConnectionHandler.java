package com.atycoin.network;

import com.atycoin.network.commands.NetworkCommand;
import com.atycoin.network.commands.NetworkCommandFactory;

import java.io.*;
import java.net.Socket;

public class ConnectionHandler implements Runnable {
    private Socket receivingConnection;
    private BufferedReader input;
    private BufferedWriter output;
    private int nodeAddress;

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
            throw new RuntimeException(e);
        } finally {
            stopConnection();
        }
    }

    private void getStreamOutput() throws IOException {
        output = new BufferedWriter(new OutputStreamWriter(receivingConnection.getOutputStream()));
        output.flush();
    }

    private void getStreamInput() throws IOException {
        input = new BufferedReader(new InputStreamReader(receivingConnection.getInputStream()));
    }

    private void stopConnection() {
        try {
            input.close();
            receivingConnection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}