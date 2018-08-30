package com.atycoin;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Transaction {
    public static final int reward = 10;

    byte[] id;
    ArrayList<TransactionInput> inputs;
    ArrayList<TransactionOutput> outputs;

    private Transaction(ArrayList<TransactionInput> inputs, ArrayList<TransactionOutput> outputs) {
        this.inputs = inputs;
        this.outputs = outputs;
    }

    // creates a new coinbase transaction
    public static Transaction newCoinbaseTransaction(String to, String data) {
        //Reward after genesis block
        if (data.isEmpty()) {
            data = String.format("Reward to '%s'", to);
        }

        //Only one input, With this information
        //  1- prevTransactionId is empty
        //  2- transactionOutputIndex is -1
        //  3- Any arbitrary data
        TransactionInput transactionInput = new TransactionInput(new byte[0], -1, data);

        //Reward to miner
        TransactionOutput transactionOutput = new TransactionOutput(reward, to);

        ArrayList<TransactionInput> transactionInputs = new ArrayList<>();
        transactionInputs.add(transactionInput);

        ArrayList<TransactionOutput> transactionOutputs = new ArrayList<>();
        transactionOutputs.add(transactionOutput);

        Transaction coinbaseTransaction = new Transaction(transactionInputs, transactionOutputs);
        coinbaseTransaction.setID();
        return coinbaseTransaction;
    }

    public void setID() {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            //little-endian
            buffer.write(Util.changeByteOrderEndianSystem(Util.intToBytes(reward)));

            //Already little-endian from input itself
            for (TransactionInput transactionInput : inputs) {
                buffer.write(transactionInput.concatenateTransactionInputData());
            }

            //Already little-endian from output itself
            for (TransactionOutput transactionOutput : outputs) {
                buffer.write(transactionOutput.concatenateTransactionOutputData());
            }

            id = Util.applySha256(buffer.toByteArray());

            // Big-endian
            id = Util.changeByteOrderEndianSystem(id);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isCoinbase() {
        // Coinbase have:
        // 1- One inputs and,
        // 2- this input have empty previous transaction hash, and
        // 3- outputIndex of this input is -1
        return inputs.size() == 1 &&
                inputs.get(0).prevTransactionId.length == 0 &&
                inputs.get(0).transactionOutputIndex == -1;
    }

//    public static Transaction newUTXOTransaction(String from, String to, int amount, Blockchain blockchain) {
//        ArrayList<TransactionInput> inputs;
//        ArrayList<TransactionOutput> outputs;
//
//        HashMap<String, ArrayList<Integer>> validOutputs = blockchain.findSpendableOutputs(from, amount);
//
//    }
}