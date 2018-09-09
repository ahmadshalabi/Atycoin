package com.atycoin;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

// represents a transaction input
public class TransactionInput {
    private final byte[] transactionID;
    private final int outputIndex;
    private byte[] signature;
    private byte[] rawPublicKey;

    public TransactionInput(byte[] transactionID, int outputIndex, byte[] rawPublicKey) {
        this.transactionID = transactionID;
        this.outputIndex = outputIndex;
        this.rawPublicKey = rawPublicKey;
        signature = new byte[0];
    }

    public TransactionInput(byte[] transactionID, int outputIndex,
                            byte[] signature, byte[] rawPublicKey) {

        this(transactionID, outputIndex, rawPublicKey);
        this.signature = signature;
    }

    public byte[] concatenateData() {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            // little-endian
            buffer.write(Util.reverseBytesOrder(transactionID));
            buffer.write(Util.reverseBytesOrder(Util.intToBytes(outputIndex)));
            buffer.write(Util.reverseBytesOrder(rawPublicKey));

            return buffer.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] getTransactionID() {
        return transactionID;
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
}