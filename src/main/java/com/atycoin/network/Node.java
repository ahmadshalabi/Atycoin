package com.atycoin.network;

import com.atycoin.AtycoinStart;
import com.atycoin.network.messages.KnownNodes;
import com.atycoin.network.messages.NetworkMessage;
import com.atycoin.network.messages.VersionMessage;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Node implements Runnable {
    private static final int NODE_VERSION = 1;
    private static String miningAddress;
    private static Node instance;

    private final ExecutorService pool;
    private int nodeAddress;

    private Socket connection;
    private BufferedWriter output;

    private Node(int nodeAddress, String minerAddress) {
        this.nodeAddress = nodeAddress;
        miningAddress = minerAddress;
        pool = Executors.newFixedThreadPool(8);
    }

    public static Node getInstance(String miningAddress) {
        if (instance == null) {
            instance = new Node(AtycoinStart.getNodeID(), miningAddress);
        }
        return instance;
    }

    public static String getMiningAddress() {
        return miningAddress;
    }

    private void startServer() {
        try (ServerSocket server = new ServerSocket(nodeAddress)) {
            if (nodeAddress != KnownNodes.get(0)) {
                NetworkMessage version = new VersionMessage(NODE_VERSION, nodeAddress);
                String request = version.makeRequest();

                connection = new Socket(InetAddress.getLocalHost(), KnownNodes.get(0));

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
            pool.shutdown();
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