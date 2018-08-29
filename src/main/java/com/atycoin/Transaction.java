package com.atycoin;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Transaction {
    public static final int reward = 50;

    byte[] transactionId;
    ArrayList<TransactionInput> transactionInputs;
    ArrayList<TransactionOutput> transactionOutputs;

    private Transaction(ArrayList<TransactionInput> transactionInputs, ArrayList<TransactionOutput> transactionOutputs) {
        this.transactionInputs = transactionInputs;
        this.transactionOutputs = transactionOutputs;

        transactionId = Util.applySha256(concatenateTransactionData());
    }

    // creates a new coinbase transaction
    public static Transaction newCoinbaseTX(String to, String data) {
        //Reward after genesis block
        if (data.isEmpty()) {
            data = String.format("Reward to '%s'", to);
        }

        //Only on input, prevTransactionOutputId is empty, transactionOutputs equal to -1
        //coinbase transaction doesn't store a script in
        // scriptSig (Arbitrary data is stored there)
        TransactionInput transactionInput = new TransactionInput(new byte[0], -1, data);

        //reward is a reward
        TransactionOutput transactionOutput = new TransactionOutput(reward, to);

        ArrayList<TransactionInput> transactionInputs = new ArrayList<>();
        transactionInputs.add(transactionInput);

        ArrayList<TransactionOutput> transactionOutputs = new ArrayList<>();
        transactionOutputs.add(transactionOutput);

        return new Transaction(transactionInputs, transactionOutputs);
    }

    public byte[] concatenateTransactionData() {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            buffer.write(Util.changeByteOrderEndianSystem(Util.intToBytes(reward)));

            for (TransactionInput transactionInput : transactionInputs) {
                buffer.write(Util.changeByteOrderEndianSystem(transactionInput.concatenateTransactionInputData()));
            }

            for (TransactionOutput transactionOutput : transactionOutputs) {
                buffer.write(Util.changeByteOrderEndianSystem(transactionOutput.concatenateTransactionOutputData()));
            }
            return buffer.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}