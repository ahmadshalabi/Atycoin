package com.atycoin;

import com.atycoin.utility.Bytes;
import com.atycoin.utility.Constant;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;

public class TransactionInput {
    private final byte[] referenceTransaction;
    private final int outputIndex;
    private byte[] rawPublicKey;
    private byte[] signature;

    public TransactionInput(byte[] referenceTransaction, int outputIndex, byte[] rawPublicKey) {
        this.referenceTransaction = referenceTransaction;
        this.outputIndex = outputIndex;
        this.rawPublicKey = rawPublicKey;
        signature = Constant.EMPTY_BYTE_ARRAY;
    }

    public byte[] concatenateData() {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            buffer.write(referenceTransaction);
            buffer.write(Bytes.toBytes(outputIndex));
            buffer.write(rawPublicKey);
            return buffer.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] getReferenceTransaction() {
        return referenceTransaction;
    }

    public int getOutputIndex() {
        return outputIndex;
    }

    public byte[] getSignature() {
        return signature;
    }

    public void setSignature(byte[] signature) {
        this.signature = signature;
    }

    public byte[] getRawPublicKey() {
        return rawPublicKey;
    }

    public void setRawPublicKey(byte[] rawPublicKey) {
        this.rawPublicKey = rawPublicKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionInput input = (TransactionInput) o;
        return outputIndex == input.outputIndex &&
                Arrays.equals(referenceTransaction, input.referenceTransaction) &&
                Arrays.equals(rawPublicKey, input.rawPublicKey) &&
                Arrays.equals(signature, input.signature);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(outputIndex);
        result = 31 * result + Arrays.hashCode(referenceTransaction);
        result = 31 * result + Arrays.hashCode(rawPublicKey);
        result = 31 * result + Arrays.hashCode(signature);
        return result;
    }

    @Override
    public String toString() {
        return new StringJoiner("\n", "", "\n")
                .add(String.format("\t\tReference transaction: %s", Bytes.toHex(referenceTransaction)))
                .add(String.format("\t\tOutput index: %d", outputIndex))
                .add(String.format("\t\tPublic key: %s", Bytes.toHex(rawPublicKey)))
                .add(String.format("\t\tSignature: %s", Bytes.toHex(signature)))
                .toString();
    }
}