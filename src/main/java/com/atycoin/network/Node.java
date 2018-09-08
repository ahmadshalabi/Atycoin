package com.atycoin.network;

import com.atycoin.network.messages.NetworkMessage;
import com.atycoin.network.messages.VersionMessage;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Node implements Runnable {
    private static String miningAddress;
    private static ArrayList<Integer> knownNodes; // To simulate DNS Seed

    static {
        knownNodes = new ArrayList<>();
        knownNodes.add(3000);
    }

    private final int NODE_VERSION = 1;
    private final ExecutorService pool;
    private int nodeAddress;
    private String minerAddress;
    private Socket connection;
    private BufferedWriter output;

    public Node(int nodeAddress, String minerAddress) {
        this.nodeAddress = nodeAddress;
        this.minerAddress = minerAddress;

        miningAddress = minerAddress;


        pool = Executors.newFixedThreadPool(8);
    }

    public static ArrayList<Integer> getKnownNodes() {
        return knownNodes;
    }

    public static String getMinerAddress() {
        return miningAddress;
    }

    public void startServer() {
        try (ServerSocket server = new ServerSocket(nodeAddress)) {
            if (nodeAddress != knownNodes.get(0)) {
                NetworkMessage version = new VersionMessage(NODE_VERSION, nodeAddress);
                String request = version.makeRequest();

                connection = new Socket(InetAddress.getLocalHost(), knownNodes.get(0));

                getOutputStream();
                output.write(request);
                output.flush();

                output.close();
                connection.close();
            }

            while (true) {
                connection = server.accept();
                handleConnection(connection, nodeAddress);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleConnection(Socket connection, int nodeAddress) {
        pool.execute(new ConnectionHandler(connection, nodeAddress));
    }

    private void getOutputStream() throws IOException {
        output = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
        output.flush();
    }

    @Override
    public void run() {
        startServer();
    }
}