package com.atycoin.network.commands;

import com.atycoin.Block;
import com.atycoin.Blockchain;
import com.atycoin.Transaction;
import com.atycoin.TransactionProcessor;
import com.atycoin.network.Mempool;
import com.atycoin.network.Node;
import com.atycoin.network.messages.InventoryMessage;
import com.atycoin.network.messages.NetworkMessage;
import com.atycoin.network.messages.TransactionMessage;
import com.atycoin.utility.Hash;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class TransactionCommandHandler extends NetworkCommand {

    @Override
    public void execute(String message, int nodeAddress) {
        Gson gson = new Gson();

        TransactionMessage remoteMessage = gson.fromJson(message, TransactionMessage.class);

        Transaction transaction = remoteMessage.getTransaction();

        TransactionProcessor transactionProcessor = new TransactionProcessor(transaction);
        if (transactionProcessor.verifyTransaction()) {
            Mempool.addItem(transaction);
        } else {
            System.out.println("Trying adding invalid transaction to mempool");
            return;
        }

        List<Integer> knownNodes = Node.getKnownNodes();
        if (nodeAddress == knownNodes.get(0)) {
            List<String> transactions = new ArrayList<>();
            transactions.add(Hash.serialize(transaction.getId()));
            for (int node : knownNodes) {
                if (node != nodeAddress && node != remoteMessage.getSenderAddress()) {
                    NetworkMessage inventoryMessage = new InventoryMessage(nodeAddress, "tx", transactions);
                    send(node, inventoryMessage);
                }
            }
        } else {
            if (Mempool.size() >= 2 && Node.getMiningAddress().length() > 0) {
                while (Mempool.size() > 0) {
                    Blockchain blockchain = Blockchain.getInstance();

                    List<Transaction> validTransactions = new ArrayList<>();
                    Transaction coinbaseTx = Transaction.newCoinbaseTransaction(Node.getMiningAddress());
                    validTransactions.add(coinbaseTx);
                    validTransactions.addAll(Mempool.values());

                    Block newBlock = blockchain.mineBlock(validTransactions);

                    System.out.println("New block is mined!");

                    Mempool.remove(validTransactions.subList(1, validTransactions.size()));

                    ArrayList<String> blockHash = new ArrayList<>();
                    blockHash.add(Hash.serialize(newBlock.getHash()));

                    for (int node : Node.getKnownNodes()) {
                        if (node != nodeAddress) {
                            NetworkMessage inventoryMessage = new InventoryMessage(nodeAddress, "block", blockHash);
                            send(node, inventoryMessage);
                        }
                    }
                }
            }
        }
    }
}