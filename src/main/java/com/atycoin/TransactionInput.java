package com.atycoin;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
            // little-endian
            buffer.write(Util.reverseBytesOrder(referenceTransaction));
            buffer.write(Util.reverseBytesOrder(Util.intToBytes(outputIndex)));
            buffer.write(Util.reverseBytesOrder(rawPublicKey));

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
    public String toString() {
        return new StringJoiner("\n", "", "\n")
                .add(String.format("\t\tReference transaction: %s", Util.bytesToHex(referenceTransaction)))
                .add(String.format("\t\tOutput index: %d", outputIndex))
                .add(String.format("\t\tPublic key: %s", Util.bytesToHex(rawPublicKey)))
                .add(String.format("\t\tSignature: %s", Util.bytesToHex(signature)))
                .toString();
    }
}