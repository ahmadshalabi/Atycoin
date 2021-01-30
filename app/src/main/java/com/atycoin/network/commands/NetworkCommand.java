package com.atycoin.network.commands;

import com.atycoin.network.messages.NetworkMessage;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

public abstract class NetworkCommand {
    private BufferedWriter output;

    abstract public void execute(String message, int nodeAddress);

    protected void send(int recipient, NetworkMessage message) {
        try (Socket sendingConnection = new Socket(InetAddress.getLocalHost(), recipient)) {
            getOutputStream(sendingConnection);
            String request = message.makeRequest();
            output.write(request);
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