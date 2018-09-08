package com.atycoin.network.commands;

import com.atycoin.Block;
import com.atycoin.Blockchain;
import com.atycoin.UTXOSet;
import com.atycoin.Util;
import com.atycoin.network.BlocksInTransit;
import com.atycoin.network.messages.BlockMessage;
import com.atycoin.network.messages.GetDataMessage;
import com.atycoin.network.messages.NetworkMessage;
import com.atycoin.network.messages.NullMessage;
import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

public class BlockCommandHandler implements NetworkCommand {
    private BufferedWriter output;

    @Override
    public void execute(String message, int nodeAddress) {
        BlockMessage remoteMessage = new Gson().fromJson(message, BlockMessage.class);

        Block block = remoteMessage.getBlock();
        System.out.println("Received a new block!");

        //TODO: check validity of incoming block before adding it
        Blockchain blockchain = Blockchain.getInstance();
        boolean isBlockAdded = blockchain.addBlock(block);

        if (isBlockAdded) {
            System.out.printf("Added block %s%n", Util.serializeHash(block.getHash()));
            UTXOSet utxoSet = UTXOSet.getInstance();
            utxoSet.update(block);
        } else {
            System.out.printf("Block %s already exist%n", Util.serializeHash(block.getHash()));
        }

        NetworkMessage responseMessage = new NullMessage();
        if (BlocksInTransit.size() > 0) {
            String blockHash = BlocksInTransit.getItem(BlocksInTransit.size() - 1);
            responseMessage = new GetDataMessage(nodeAddress, "block", blockHash);
            BlocksInTransit.removeItem(BlocksInTransit.size() - 1);
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