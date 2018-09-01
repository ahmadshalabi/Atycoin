package com.atycoin;

import org.bouncycastle.util.encoders.Hex;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Transaction {
    public static final int reward = 10;

    byte[] transactionId;
    ArrayList<TransactionInput> inputs;
    ArrayList<TransactionOutput> outputs;

    private Transaction(ArrayList<TransactionInput> inputs, ArrayList<TransactionOutput> outputs) {
        this.inputs = inputs;
        this.outputs = outputs;
    }

    // creates a new coinbase transaction (to : address in BASE58)
    public static Transaction newCoinbaseTransaction(String to) {
        byte[] arbitraryData = Util.applySHA256(String.format("Reward to '%s'", to).getBytes());

        //Only one input, With this information
        //  1- prevTransactionId is empty
        //  2- transactionOutputIndex is -1
        //  3- Any arbitrary data
        TransactionInput transactionInput = new TransactionInput(new byte[0], -1, arbitraryData);

        //Reward to miner
        TransactionOutput transactionOutput = TransactionOutput.newTXOutput(reward, to);

        ArrayList<TransactionInput> transactionInputs = new ArrayList<>();
        transactionInputs.add(transactionInput);

        ArrayList<TransactionOutput> transactionOutputs = new ArrayList<>();
        transactionOutputs.add(transactionOutput);

        Transaction coinbaseTransaction = new Transaction(transactionInputs, transactionOutputs);
        coinbaseTransaction.setTransactionId();
        return coinbaseTransaction;
    }

    //TODO: It need a lot of clean ^_^
    //from and to (Address in BASE58)
    public static Transaction newUTXOTransaction(String from, String to, int amount) {
        HashMap<String, ArrayList<Integer>> unspentOutputs = new HashMap<>();

        Blockchain blockchain = Blockchain.getInstance();

        WalletsProcessor walletsProcessor = WalletsProcessor.newWalletsProcessor();

        //Get wallet rather than decode from address
        // Because I need rawPublicKey that sored in wallet
        // when reference transactionInput as spent
        Wallet wallet = walletsProcessor.getWallet(from);
        byte[] fromPublicKeyHashed = Wallet.hashPublicKey(wallet.getRawPublicKey());

        ArrayList<Transaction> unspentTransactions = blockchain.findUnspentTransaction(fromPublicKeyHashed);
        int accumulated = 0;

        boolean isAmountReached = false;

        // find unspentOutputs unlocked by (from)
        for (Transaction unspentTransaction : unspentTransactions) {
            String txID = Util.bytesToHex(unspentTransaction.transactionId);

            for (TransactionOutput transactionOutput : unspentTransaction.outputs) {
                if (transactionOutput.isLockedWithKey(fromPublicKeyHashed) && accumulated < amount) {

                    accumulated += transactionOutput.value;

                    ArrayList<Integer> list = unspentOutputs.get(txID);
                    if (list == null) {
                        list = new ArrayList<>();
                        list.add(unspentTransaction.outputs.indexOf(transactionOutput));
                        unspentOutputs.put(txID, list);
                    } else {
                        list.add(unspentTransaction.outputs.indexOf(transactionOutput));
                    }

                    if (accumulated >= amount) {
                        isAmountReached = true;
                        break;
                    }
                }
            }

            if (isAmountReached) {
                break;
            }
        }

        if (accumulated < amount) {
            System.out.println("ERROR: Not enough funds");
            return null;
        }

        ArrayList<TransactionInput> inputs = new ArrayList<>();
        ArrayList<TransactionOutput> outputs = new ArrayList<>();

        //Build a list of inputs
        for (Map.Entry<String, ArrayList<Integer>> entry : unspentOutputs.entrySet()) {
            byte[] transactionId = Hex.decode(entry.getKey());
            for (int transactionOutputIndex : entry.getValue()) {
                inputs.add(new TransactionInput(transactionId, transactionOutputIndex, wallet.getRawPublicKey()));
            }
        }

        //Build a list of outputs
        outputs.add(TransactionOutput.newTXOutput(amount, to));
        if (accumulated > amount) {
            int change = accumulated - amount;
            outputs.add(TransactionOutput.newTXOutput(change, from));
        }

        Transaction newTransaction = new Transaction(inputs, outputs);
        newTransaction.setTransactionId();

        return newTransaction;
    }


    public void setTransactionId() {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            //little-endian
            buffer.write(Util.reverseBytesOrder(Util.intToBytes(reward)));

            //Already little-endian from input itself
            for (TransactionInput transactionInput : inputs) {
                buffer.write(transactionInput.concatenateTransactionInputData());
            }

            //Already little-endian from output itself
            for (TransactionOutput transactionOutput : outputs) {
                buffer.write(transactionOutput.concatenateTransactionOutputData());
            }

            transactionId = Util.applySHA256(buffer.toByteArray());

            // Big-endian
            transactionId = Util.reverseBytesOrder(transactionId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isCoinbaseTransaction() {
        // Coinbase have:
        // 1- One inputs and,
        // 2- this input have empty previous transaction hash, and
        // 3- outputIndex of this input is -1
        return inputs.size() == 1 &&
                inputs.get(0).prevTransactionId.length == 0 &&
                inputs.get(0).transactionOutputIndex == -1;
    }
}