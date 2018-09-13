package com.atycoin;

import com.atycoin.utility.Address;
import com.atycoin.utility.Bytes;
import com.atycoin.utility.Constant;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;

public class TransactionOutput {
    private final int value;
    private byte[] publicKeyHashed;

    public TransactionOutput(int value, byte[] publicKeyHashed) {
        this.value = value;
        this.publicKeyHashed = publicKeyHashed;
    }

    public static TransactionOutput newTransactionOutput(int value, String address) {
        TransactionOutput transactionOutput = new TransactionOutput(value, Constant.EMPTY_BYTE_ARRAY);
        transactionOutput.lock(address);
        return transactionOutput;
    }

    // signs the output
    private void lock(String address) {
        publicKeyHashed = Address.getPublicKeyHashed(address);
    }

    public boolean isLockedWithKey(byte[] publicKeyHashed) {
        return Arrays.equals(this.publicKeyHashed, publicKeyHashed);
    }

    public byte[] concatenateData() {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            buffer.write(Bytes.toBytes(value));
            buffer.write(publicKeyHashed);
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionOutput that = (TransactionOutput) o;
        return value == that.value &&
                Arrays.equals(publicKeyHashed, that.publicKeyHashed);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(value);
        result = 31 * result + Arrays.hashCode(publicKeyHashed);
        return result;
    }

    @Override
    public String toString() {
        return new StringJoiner("\n")
                .add(String.format("\t\tValue: %d", value))
                .add(String.format("\t\tPublic Key Hashed: %s", Bytes.toHex(publicKeyHashed)))
                .toString();
    }
}