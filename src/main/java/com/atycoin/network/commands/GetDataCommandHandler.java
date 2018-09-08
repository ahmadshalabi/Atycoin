package com.atycoin.network.commands;

import com.atycoin.Block;
import com.atycoin.Blockchain;
import com.atycoin.Transaction;
import com.atycoin.network.Mempool;
import com.atycoin.network.messages.*;
import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

//TODO: check if we actually have this block/transaction
public class GetDataCommandHandler implements NetworkCommand {
    private BufferedWriter output;

    @Override
    public void execute(String message, int nodeAddress) {
        GetDataMessage remoteMessage = new Gson().fromJson(message, GetDataMessage.class);
        String type = remoteMessage.getType();

        NetworkMessage responseMessage = new NullMessage();
        if (type.equals("block")) {
            Blockchain blockchain = Blockchain.getInstance();
            String blockHash = remoteMessage.getId();
            Block block = blockchain.getBlock(blockHash);

            responseMessage = new BlockMessage(nodeAddress, block);
        } else if (type.equals("tx")) {
            String transactionID = remoteMessage.getId();
            Transaction transaction = Mempool.getItem(transactionID);

            responseMessage = new TransactionMessage(nodeAddress, transaction);
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