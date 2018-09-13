package com.atycoin;

import com.atycoin.database.ChainStateDAO;
import com.atycoin.utility.Bytes;
import com.atycoin.utility.Constant;
import com.atycoin.utility.Hash;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

public class Transaction {
    private static final byte[] COINBASE_ID = Constant.EMPTY_BYTE_ARRAY;
    private static final int COINBASE_INDEX = -1;
    private static final int reward = 10; //TODO: Make reward adjustable
    private final List<TransactionInput> inputs;
    private final List<TransactionOutput> outputs;
    private final long timestamp;
    private byte[] id;

    private Transaction(List<TransactionInput> inputs, List<TransactionOutput> outputs) {
        this.inputs = inputs;
        this.outputs = outputs;
        timestamp = System.currentTimeMillis() / 1000L; // Convert to Second
    }

    // creates a trimmed copy of Transaction to be used in signing and verifying
    public static Transaction trimmedTransaction(Transaction transaction) {
        List<TransactionInput> inputs = new ArrayList<>();
        for (TransactionInput input : transaction.getInputs()) {
            byte[] id = input.getReferenceTransaction();
            int index = input.getOutputIndex();
            inputs.add(new TransactionInput(id, index, Constant.EMPTY_BYTE_ARRAY));
        }

        List<TransactionOutput> outputs = new ArrayList<>();
        for (TransactionOutput output : transaction.getOutputs()) {
            int value = output.getValue();
            byte[] publicKeyHashed = output.getPublicKeyHashed();
            outputs.add(new TransactionOutput(value, publicKeyHashed));
        }
        return new Transaction(inputs, outputs);
    }

    //Reward to miner
    public static Transaction newCoinbaseTransaction(String to) {
        Random random = new Random();
        byte[] arbitraryData = new byte[20];
        random.nextBytes(arbitraryData);

        TransactionInput input = new TransactionInput(COINBASE_ID, COINBASE_INDEX, arbitraryData);
        List<TransactionInput> inputs = new ArrayList<>();
        inputs.add(input);

        TransactionOutput output = TransactionOutput.newTransactionOutput(reward, to);
        List<TransactionOutput> outputs = new ArrayList<>();
        outputs.add(output);

        Transaction coinbaseTransaction = new Transaction(inputs, outputs);
        coinbaseTransaction.setID();
        return coinbaseTransaction;
    }

    //recipient : Base58 Address
    public static Transaction newTransaction(Wallet sender, String recipient, int amount) {
        // get unspent outputs to reference in inputs
        Map<String, List<Integer>> unspentOutputs = ChainState.getInstance().findUnspentOutputs(sender, amount);

        int balance = getBalance(unspentOutputs);
        if (balance < amount) {
            System.out.println("ERROR: Not enough funds");
            return null;
        }

        List<TransactionInput> inputs = buildInputs(sender, unspentOutputs);
        List<TransactionOutput> outputs = buildOutputs(sender, balance, recipient, amount);

        Transaction newTransaction = new Transaction(inputs, outputs);
        newTransaction.setID();
        return newTransaction;
    }

    private static int getBalance(Map<String, List<Integer>> unspentOutputs) {
        int balance = 0;
        for (Map.Entry<String, List<Integer>> unspentOutputReferences : unspentOutputs.entrySet()) {
            String transactionID = unspentOutputReferences.getKey();
            List<TransactionOutput> outputs = ChainStateDAO.getInstance().getReferenceOutputs(transactionID);

            List<Integer> unspentOutputIndices = unspentOutputReferences.getValue();
            for (int index : unspentOutputIndices) {
                TransactionOutput unspentOutput = outputs.get(index);
                balance += unspentOutput.getValue();
            }
        }
        return balance;
    }

    private static List<TransactionInput> buildInputs(Wallet sender, Map<String, List<Integer>> unspentOutputs) {
        byte[] publicKey = sender.getRawPublicKey();
        List<TransactionInput> inputs = new ArrayList<>();
        for (Map.Entry<String, List<Integer>> entry : unspentOutputs.entrySet()) {
            String key = entry.getKey();
            byte[] transactionId = Hash.deserialize(key);
            for (int transactionOutputIndex : entry.getValue()) {
                inputs.add(new TransactionInput(transactionId, transactionOutputIndex, publicKey));
            }
        }
        return inputs;
    }

    private static List<TransactionOutput> buildOutputs(Wallet sender, int balance, String recipient, int amount) {
        List<TransactionOutput> outputs = new ArrayList<>();
        outputs.add(TransactionOutput.newTransactionOutput(amount, recipient));
        if (balance > amount) {
            int change = balance - amount;
            outputs.add(TransactionOutput.newTransactionOutput(change, sender.getAddress()));
        }
        return outputs;
    }

    //Coinbase: One input, empty transactionID and -1 index
    public boolean isCoinbaseTransaction() {
        TransactionInput input = inputs.get(0);
        return inputs.size() == 1 &&
                input.getReferenceTransaction().length == 0 &&
                input.getOutputIndex() == COINBASE_INDEX;
    }

    @Override
    public String toString() {
        StringJoiner stringJoiner = new StringJoiner("\n", "", "\n");
        stringJoiner.add(String.format("--- Transaction %s:", Bytes.toHex(id)));

        TransactionInput input;
        for (int i = 0, size = inputs.size(); i < size; i++) {
            input = inputs.get(i);
            stringJoiner.add(String.format("\tInput %d:", i));
            stringJoiner.add(input.toString());
        }

        TransactionOutput output;
        for (int i = 0, size = outputs.size(); i < size; i++) {
            output = outputs.get(i);
            stringJoiner.add(String.format("\tOutput %d:", i));
            stringJoiner.add(output.toString());
        }
        return stringJoiner.toString();
    }

    public byte[] getId() {
        return id;
    }

    public List<TransactionInput> getInputs() {
        return inputs;
    }

    public List<TransactionOutput> getOutputs() {
        return outputs;
    }

    public void setID() {
        byte[] unHashedID = getUnHashedID();
        id = Hash.applySHA256(unHashedID);
    }

    private byte[] getUnHashedID() {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            buffer.write(Bytes.toBytes(reward));

            for (TransactionInput transactionInput : inputs) {
                buffer.write(transactionInput.concatenateData());
            }

            for (TransactionOutput transactionOutput : outputs) {
                buffer.write(transactionOutput.concatenateData());
            }

            buffer.write(Bytes.toBytes(timestamp));
            return buffer.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}