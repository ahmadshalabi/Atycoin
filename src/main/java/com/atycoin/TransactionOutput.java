package com.atycoin;

import com.atycoin.utility.Bytes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.StringJoiner;

public class TransactionOutput {
    private final int value;
    private byte[] publicKeyHashed;

    public TransactionOutput(int value, byte[] publicKeyHashed) {
        this.value = value;
        this.publicKeyHashed = publicKeyHashed;
    }

    public static TransactionOutput newTXOutput(int value, String address) {
        TransactionOutput transactionOutput = new TransactionOutput(value, Constant.EMPTY_BYTE_ARRAY);
        transactionOutput.lock(address);

        return transactionOutput;
    }

    // signs the output
    private void lock(String address) {
        byte[] fullPayload = Base58.decode(address);
        publicKeyHashed = Arrays.copyOfRange(fullPayload, 1, fullPayload.length - 4);
    }

    //checks if the output can be unlocked with the provided data
    public boolean isLockedWithKey(byte[] publicKeyHashed) {
        return Arrays.equals(this.publicKeyHashed, publicKeyHashed);
    }

    public byte[] concatenateData() {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            //little-endian
            buffer.write(Bytes.reverseOrder(Bytes.toBytes(value)));
            buffer.write(Bytes.reverseOrder(publicKeyHashed));

            return buffer.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int getValue() {
        return value;
    }

    public byte[] getPublicKeyHashed() {
        return publicKeyHashed;
    }

    @Override
    public String toString() {
        return new StringJoiner("\n")
                .add(String.format("\t\tValue: %d", value))
                .add(String.format("\t\tPublic Key Hashed: %s", Bytes.toHex(publicKeyHashed)))
                .toString();
    }
}