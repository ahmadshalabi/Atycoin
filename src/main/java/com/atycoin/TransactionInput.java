package com.atycoin;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

// represents a transaction input
public class TransactionInput {
    byte[] prevTransactionId;
    int transactionOutputIndex;
    byte[] signature;
    byte[] publicKey;

    public TransactionInput(byte[] prevTransactionId, int transactionOutputIndex, byte[] publicKey) {
        this.prevTransactionId = prevTransactionId;
        this.transactionOutputIndex = transactionOutputIndex;
        this.publicKey = publicKey;
    }

    public byte[] concatenateTransactionInputData() {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            // little-endian
            buffer.write(Util.reverseBytesOrder(prevTransactionId));
            buffer.write(Util.reverseBytesOrder(Util.intToBytes(transactionOutputIndex)));
            buffer.write(Util.reverseBytesOrder(publicKey));

            return buffer.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // checks checks whether the address initiated the transaction
    public boolean usesKey(byte[] publicKeyHash) {
        byte[] lockingHash = Wallet.hashPublicKey(publicKey);
        return Arrays.equals(lockingHash, publicKeyHash);
    }
}