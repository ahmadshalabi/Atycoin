package com.atycoin;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

// represents a transaction output
public class TransactionOutput {
    public int value;
    byte[] publicKeyHashed;

    private TransactionOutput(int value) {
        this.value = value;
    }

    public static TransactionOutput newTXOutput(int value, String address) {
        TransactionOutput transactionOutput = new TransactionOutput(value);
        transactionOutput.lock(address);

        return transactionOutput;
    }

    // lock signs the output
    public void lock(String address) {
        byte[] fullPayload = Base58.decode(address);
        publicKeyHashed = Arrays.copyOfRange(fullPayload, 1, fullPayload.length - 4);
    }

    //checks if the output can be unlocked with the provided data
    public boolean isLockedWithKey(byte[] publicKeyHashed) {
        return Arrays.equals(this.publicKeyHashed, publicKeyHashed);
    }

    public byte[] concatenateTransactionOutputData() {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            //little-endian
            buffer.write(Util.reverseBytesOrder(Util.intToBytes(value)));
            buffer.write(Util.reverseBytesOrder(publicKeyHashed));

            return buffer.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}