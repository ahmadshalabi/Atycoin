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

    public static TransactionOutput newTXOutput(int value, byte[] address) {
        TransactionOutput newTXO = new TransactionOutput(value);
        newTXO.lock(address);

        return newTXO;
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

    // lock signs the output
    public void lock(byte[] address) {
        byte[] fullPayload = Base58.decode(address);
        publicKeyHashed = Arrays.copyOfRange(fullPayload, 1, fullPayload.length - 4);
    }

    //checks if the output can be unlocked with the provided data
    public boolean isLockedWithKey(byte[] publicKeyHased) {
        return Arrays.equals(this.publicKeyHashed, publicKeyHased);
    }
}