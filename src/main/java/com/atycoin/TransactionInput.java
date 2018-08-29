package com.atycoin;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class TransactionInput {
    byte[] prevTransactionOutputId;
    int coinAmount;
    String scriptSig;

    public TransactionInput(byte[] prevTransactionOutputId, int coinAmount, String scriptSig) {
        this.prevTransactionOutputId = prevTransactionOutputId;
        this.coinAmount = coinAmount;
        this.scriptSig = scriptSig;
    }

    public byte[] concatenateTransactionInputData() {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            buffer.write(Util.changeByteOrderEndianSystem(prevTransactionOutputId));
            buffer.write(Util.changeByteOrderEndianSystem(Util.intToBytes(coinAmount)));
            buffer.write(Util.changeByteOrderEndianSystem(Util.stringToBytes(scriptSig)));

            return buffer.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}