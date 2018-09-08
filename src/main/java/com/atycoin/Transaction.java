package com.atycoin;

import org.bouncycastle.jce.interfaces.ECPrivateKey;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Transaction {
    private static final int reward = 10;

    private byte[] id;
    private ArrayList<TransactionInput> inputs;
    private ArrayList<TransactionOutput> outputs;
    private long timestamp;

    private Transaction(ArrayList<TransactionInput> inputs, ArrayList<TransactionOutput> outputs) {
        this.inputs = inputs;
        this.outputs = outputs;
        timestamp = System.currentTimeMillis() / 1000L; // Convert to Second
    }

    // creates a new coinbase transaction (to : address in BASE58)
    public static Transaction newCoinbaseTransaction(String to) {
        byte[] arbitraryData = new byte[20];

        Random random = new Random();
        random.nextBytes(arbitraryData);
        //Coinbase: One inputs, empty prevTransactionHash and -1 index
        TransactionInput transactionInput = new TransactionInput(new byte[0], -1, arbitraryData);

        //Reward to miner
        TransactionOutput transactionOutput = TransactionOutput.newTXOutput(reward, to);

        ArrayList<TransactionInput> transactionInputs = new ArrayList<>();
        transactionInputs.add(transactionInput);

        ArrayList<TransactionOutput> transactionOutputs = new ArrayList<>();
        transactionOutputs.add(transactionOutput);

        Transaction coinbaseTransaction = new Transaction(transactionInputs, transactionOutputs);
        coinbaseTransaction.id = coinbaseTransaction.hash();
        return coinbaseTransaction;
    }

    //from : wallet, to : Base58 Address
    public static Transaction newUTXOTransaction(Wallet wallet, String to, int amount) {
        byte[] senderPublicKeyHashed = Wallet.hashPublicKey(wallet.getRawPublicKey());

        UTXOSet utxoSet = UTXOSet.getInstance();

        // finds and returns unspent outputs to reference in inputs
        HashMap<String, ArrayList<Integer>> UTXO = utxoSet.findSpendableOutputs(senderPublicKeyHashed, amount);

        int accumulated = 0;
        //TODO: Solve recalculated accumulated balance of SpendableOutputs
        for (Map.Entry<String, ArrayList<Integer>> entry : UTXO.entrySet()) {
            ArrayList<TransactionOutput> outputs = utxoSet.getTxOutputs(entry.getKey());
            for (int index : entry.getValue()) {
                TransactionOutput transactionOutput = outputs.get(index);
                accumulated += transactionOutput.getValue();
            }
        }

        if (accumulated < amount) {
            System.out.println("ERROR: Not enough funds");
            return null;
        }

        ArrayList<TransactionInput> inputs = new ArrayList<>();
        ArrayList<TransactionOutput> outputs = new ArrayList<>();

        //Build a list of inputs
        for (Map.Entry<String, ArrayList<Integer>> entry : UTXO.entrySet()) {
            byte[] transactionId = Util.deserializeHash(entry.getKey());
            for (int transactionOutputIndex : entry.getValue()) {
                inputs.add(new TransactionInput(transactionId, transactionOutputIndex, wallet.getRawPublicKey()));
            }
        }

        //Build a list of outputs
        outputs.add(TransactionOutput.newTXOutput(amount, to));
        if (accumulated > amount) {
            int change = accumulated - amount;
            outputs.add(TransactionOutput.newTXOutput(change, wallet.getAddress()));
        }

        Transaction newTransaction = new Transaction(inputs, outputs);
        newTransaction.id = newTransaction.hash();

        Blockchain blockchain = Blockchain.getInstance();
        boolean isSigningCorrectly = blockchain.signTransaction(newTransaction, wallet.getPrivateKey());

        if (!isSigningCorrectly) {
            return null;
        }

        return newTransaction;
    }

    // signs each input of a Transaction
    public void sign(ECPrivateKey privateKey, HashMap<String, Transaction> previousTransactions) {
        if (isCoinbaseTransaction()) {
            return;
        }

        Transaction trimmedCopy = trimmedCopy();

        ArrayList<TransactionInput> trimmedCopyInputs = trimmedCopy.getInputs();

        for (TransactionInput input : trimmedCopyInputs) {
            Transaction previousTransaction = previousTransactions.get(Util.serializeHash(input.getTransactionID()));

            input.setSignature(new byte[0]);

            ArrayList<TransactionOutput> previousTransactionOutputs = previousTransaction.getOutputs();
            TransactionOutput transactionOutput = previousTransactionOutputs.get(input.getOutputIndex());
            transactionOutput.getPublicKeyHashed();

            byte[] rawPublicKey = transactionOutput.getPublicKeyHashed();
            input.setRawPublicKey(rawPublicKey);

            trimmedCopy.id = trimmedCopy.hash();
            input.setRawPublicKey(new byte[0]);

            int txCopyIndex = trimmedCopy.inputs.indexOf(input);

            TransactionInput transactionInput = inputs.get(txCopyIndex);
            transactionInput.setSignature(Util.applyECDSASig(privateKey, trimmedCopy.getId()));
        }
    }

    // verifies signatures of Transaction inputs
    public boolean verify(Map<String, Transaction> previousTransactions) {
        Transaction trimmedCopy = trimmedCopy();

        boolean isValidTransaction = true;
        for (TransactionInput input : inputs) {
            Transaction previousTransaction = previousTransactions.get(Util.serializeHash(input.getTransactionID()));

            int inputIndex = inputs.indexOf(input);

            TransactionInput trimmedCopyInput = trimmedCopy.inputs.get(inputIndex);
            trimmedCopyInput.setSignature(new byte[0]);

            ArrayList<TransactionOutput> transactionOutputs = previousTransaction.getOutputs();
            TransactionOutput transactionOutput = transactionOutputs.get(input.getOutputIndex());
            trimmedCopyInput.setRawPublicKey(transactionOutput.getPublicKeyHashed());

            trimmedCopy.id = trimmedCopy.hash();
            trimmedCopyInput.setRawPublicKey(new byte[0]);

            isValidTransaction = Util.verifyECDSASig(Util.decodeKey(input.getRawPublicKey()),
                    trimmedCopy.id, input.getSignature());

            if (!isValidTransaction) {
                break;
            }
        }

        return isValidTransaction;
    }

    // creates a trimmed copy of Transaction to be used in signing
    public Transaction trimmedCopy() {
        ArrayList<TransactionInput> inputs = new ArrayList<>();
        ArrayList<TransactionOutput> outputs = new ArrayList<>();

        for (TransactionInput input : this.inputs) {
            inputs.add(new TransactionInput(
                    input.getTransactionID(), input.getOutputIndex(), new byte[0], new byte[0]));
        }

        for (TransactionOutput output : this.outputs) {
            outputs.add(new TransactionOutput(output.getValue(), output.getPublicKeyHashed()));
        }

        return new Transaction(inputs, outputs);
    }

    // returns the hash of the Transaction
    public byte[] hash() {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            //little-endian
            buffer.write(Util.reverseBytesOrder(Util.intToBytes(reward)));

            //Already little-endian from input itself
            for (TransactionInput transactionInput : inputs) {
                buffer.write(transactionInput.concatenateData());
            }

            //Already little-endian from output itself
            for (TransactionOutput transactionOutput : outputs) {
                buffer.write(transactionOutput.concatenateData());
            }

            buffer.write(Util.reverseBytesOrder(Util.longToBytes(timestamp)));
            // Big-endian
            return Util.reverseBytesOrder(Util.applySHA256(buffer.toByteArray()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //Coinbase: One inputs, empty prevTransactionHash and -1 index
    public boolean isCoinbaseTransaction() {
        TransactionInput transactionInput = inputs.get(0);
        return inputs.size() == 1 &&
                transactionInput.getTransactionID().length == 0 &&
                transactionInput.getOutputIndex() == -1;
    }

    // returns a human-readable representation of a transaction
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("--- Transaction %s:%n", Util.bytesToHex(id)));

        TransactionInput input;
        for (int i = 0, size = inputs.size(); i < size; i++) {
            input = inputs.get(i);
            builder.append(String.format("\tInput %d:%n", i));
            builder.append(String.format("\t\tTXID:      %s%n", Util.bytesToHex(input.getTransactionID())));
            builder.append(String.format("\t\tOutIndex:  %d%n", input.getOutputIndex()));
            builder.append(String.format("\t\tSignature: %s%n", Util.bytesToHex(input.getSignature())));
            builder.append(String.format("\t\tPubKey:    %s%n", Util.bytesToHex(input.getRawPublicKey())));
        }

        TransactionOutput output;
        for (int i = 0, size = outputs.size(); i < size; i++) {
            output = outputs.get(i);
            builder.append(String.format("\tOutput %d:%n", i));
            builder.append(String.format("\t\tValue:  %d%n", output.getValue()));
            builder.append(String.format("\t\tScript: %s%n", Util.bytesToHex(output.getPublicKeyHashed())));
        }
        return builder.toString();
    }

    public byte[] getId() {
        return id;
    }

    public ArrayList<TransactionInput> getInputs() {
        return inputs;
    }

    public ArrayList<TransactionOutput> getOutputs() {
        return outputs;
    }
}