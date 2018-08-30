package com.atycoin;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class TransactionOutput {
    public int value;
    public String scriptPubKey;

    public TransactionOutput(int value, String scriptPubKey) {
        this.value = value;
        this.scriptPubKey = scriptPubKey;
    }

    public byte[] concatenateTransactionOutputData() {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            //little-endian
            buffer.write(Util.changeByteOrderEndianSystem(Util.intToBytes(value)));
            buffer.write(Util.changeByteOrderEndianSystem(Util.stringToBytes(scriptPubKey)));

            return buffer.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //checks if the output can be unlocked with the provided data
    public boolean canBeUnlockedWith(String unlockingData) {
        return unlockingData.equals(scriptPubKey);
    }
}