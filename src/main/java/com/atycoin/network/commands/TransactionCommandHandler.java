package com.atycoin.network.commands;

import com.atycoin.Block;
import com.atycoin.Blockchain;
import com.atycoin.Transaction;
import com.atycoin.TransactionProcessor;
import com.atycoin.network.KnownNodes;
import com.atycoin.network.Mempool;
import com.atycoin.network.Node;
import com.atycoin.network.messages.InventoryMessage;
import com.atycoin.network.messages.NetworkMessage;
import com.atycoin.network.messages.TransactionMessage;
import com.atycoin.utility.Hash;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

        if (nodeAddress == KnownNodes.get(0)) {
            centralNodeResponse(transaction, nodeAddress, remoteMessage.getSenderAddress());
        } else {
            minerNodeResponse(nodeAddress);
        }
    }

    private void centralNodeResponse(Transaction transaction, int nodeAddress, int senderAddress) {
        List<String> transactions = new ArrayList<>();
        transactions.add(Hash.serialize(transaction.getId()));
        broadcastTransaction(nodeAddress, senderAddress, transactions);
    }

    private void minerNodeResponse(int nodeAddress) {
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

                List<String> blocksHash = new ArrayList<>();
                blocksHash.add(Hash.serialize(newBlock.getHash()));

                broadcastBlock(nodeAddress, blocksHash);
            }
        }
    }

    private void broadcastTransaction(int nodeAddress, int senderAddress, List<String> items) {
        for (int node : KnownNodes.getKnownNodes()) {
            if (node != nodeAddress && node != senderAddress) {
                NetworkMessage inventoryMessage = new InventoryMessage(nodeAddress, "tx", items);
                send(node, inventoryMessage);
            }
        }
    }

    private void broadcastBlock(int nodeAddress, List<String> items) {
        for (int node : KnownNodes.getKnownNodes()) {
            if (node != nodeAddress) {
                NetworkMessage inventoryMessage = new InventoryMessage(nodeAddress, "block", items);
                send(node, inventoryMessage);
                try {
                    TimeUnit.MILLISECONDS.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}