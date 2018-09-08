package com.atycoin.network.commands;

import com.atycoin.*;
import com.atycoin.network.Mempool;
import com.atycoin.network.Node;
import com.atycoin.network.messages.InventoryMessage;
import com.atycoin.network.messages.NetworkMessage;
import com.atycoin.network.messages.TransactionMessage;
import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

public class TransactionCommandHandler implements NetworkCommand {
    private BufferedWriter output;

    @Override
    public void execute(String message, int nodeAddress) {
        Gson gson = new Gson();

        TransactionMessage remoteMessage = gson.fromJson(message, TransactionMessage.class);

        Transaction transaction = remoteMessage.getTransaction();

        if (Blockchain.getInstance().verifyTransaction(transaction)) {
            Mempool.addItem(transaction);
        } else {
            System.out.println("Trying adding invalid transaction to mempool");
        }

        ArrayList<Integer> knownNodes = Node.getKnownNodes();
        if (nodeAddress == knownNodes.get(0)) {
            ArrayList<String> transactions = new ArrayList<>();
            transactions.add(Util.serializeHash(transaction.getId()));
            for (int node : knownNodes) {
                if (node != nodeAddress && node != remoteMessage.getSenderAddress()) {
                    NetworkMessage inventoryMessage = new InventoryMessage(nodeAddress, "tx", transactions);
                    try (Socket sendingConnection = new Socket(InetAddress.getLocalHost(), node)) {
                        getOutputStream(sendingConnection);

                        output.write(inventoryMessage.makeRequest());
                        output.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        } else {
            if (Mempool.size() >= 2 && Node.getMinerAddress().length() > 0) {
                while (Mempool.size() > 0) {
                    Blockchain blockchain = Blockchain.getInstance();

                    ArrayList<Transaction> validTransactions = new ArrayList<>();
                    Transaction coinbaseTx = Transaction.newCoinbaseTransaction(Node.getMinerAddress());
                    validTransactions.add(coinbaseTx);
                    validTransactions.addAll(Mempool.values());

                    Block newBlock = blockchain.mineBlock(validTransactions);
                    UTXOSet utxoSet = UTXOSet.getInstance();
                    utxoSet.update(newBlock);

                    System.out.println("New block is mined!");

                    Mempool.remove(validTransactions.subList(1, validTransactions.size()));

                    ArrayList<String> blockHash = new ArrayList<>();
                    blockHash.add(Util.serializeHash(newBlock.getHash()));

                    for (int node : Node.getKnownNodes()) {
                        if (node != nodeAddress) {
                            NetworkMessage inventoryMessage = new InventoryMessage(nodeAddress, "block", blockHash);
                            try (Socket sendingConnection = new Socket(InetAddress.getLocalHost(), node)) {
                                getOutputStream(sendingConnection);

                                output.write(inventoryMessage.makeRequest());
                                output.close();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
            }
        }
    }

    private void getOutputStream(Socket connection) throws IOException {
        output = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
        output.flush();
    }
}