package com.atycoin;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class TransactionInput {
    byte[] prevTransactionId;
    int transactionOutputIndex;
    String scriptSig;

    public TransactionInput(byte[] prevTransactionId, int transactionOutputIndex, String scriptSig) {
        this.prevTransactionId = prevTransactionId;
        this.transactionOutputIndex = transactionOutputIndex;
        this.scriptSig = scriptSig;
    }

    public byte[] concatenateTransactionInputData() {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            // little-endian
            buffer.write(Util.changeByteOrderEndianSystem(prevTransactionId));
            buffer.write(Util.changeByteOrderEndianSystem(Util.intToBytes(transactionOutputIndex)));
            buffer.write(Util.changeByteOrderEndianSystem(Util.stringToBytes(scriptSig)));

            return buffer.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // checks checks whether the address initiated the transaction
    public boolean canUnlockOutputWith(String unlockingData) {
        return unlockingData.equals(scriptSig);
    }
}