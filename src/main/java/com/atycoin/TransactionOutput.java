package com.atycoin;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class TransactionOutput {
    int coinAmount;
    String scriptPubKey;

    public TransactionOutput(int coinAmount, String scriptPubKey) {
        this.coinAmount = coinAmount;
        this.scriptPubKey = scriptPubKey;
    }

    public byte[] concatenateTransactionOutputData() {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            buffer.write(Util.changeByteOrderEndianSystem(Util.intToBytes(coinAmount)));
            buffer.write(Util.changeByteOrderEndianSystem(Util.stringToBytes(scriptPubKey)));

            return buffer.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}