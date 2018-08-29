package com.atycoin;

import com.google.gson.Gson;
import org.bouncycastle.util.Arrays;

public class Block {
    private final int targetBits = 8; //TODO: Make it Adjusted to meet some requirements
    public byte[] hashPrevBlock;
    public byte[] hashMerkleRoot;
    public long timestamp;
    public byte[] hash; // Current Block hash
    //TODO: separate relevant hashMerkleRoot in BlockHeader
    private int version = 1;
    private int nonce;

    public Block(byte[] hashMerkleRoot, byte[] hashPrevBlock) {
        timestamp = System.currentTimeMillis() / 1000L; // Convert to Second
        this.hashMerkleRoot = hashMerkleRoot;
        this.hashPrevBlock = hashPrevBlock;

        proofOfWork();
    }

    public void proofOfWork() {
        System.out.printf("Mining the block containing \"%s\"\n", new String(hashMerkleRoot));

        //TODO: Enhance checking in nonce overflow
        while (nonce < Integer.MAX_VALUE) {
            byte[] data = concatenateBlockData();

            // Double hash
            hash = Util.applySha256(Util.applySha256(data));

            // Change to Big-endian
            hash = Util.changeByteOrderEndianSystem(hash);

            if (isValidHash(hash)) {
                break;
            }

            nonce++;
        }
        System.out.println(Util.bytesToHex(hash));
        System.out.println();
    }

    public String serializeBlock() {
        Gson gson = new Gson();

        return gson.toJson(this);
    }

    // TODO: Clean isValidBlock
    public boolean isValidBlock() {
        byte[] data = concatenateBlockData();

        byte[] calculatedHash = Util.applySha256(Util.applySha256(data));

        calculatedHash = Util.changeByteOrderEndianSystem(calculatedHash);

        if (!isValidHash(calculatedHash)) {
            return false;
        }

        return Arrays.areEqual(hash, calculatedHash);
    }

    private byte[] concatenateBlockData() {
        //TODO: Consider more efficient way to concatenate byte[] arrays

        //Change to little-endian
        byte[] tmp = Arrays.concatenate(
                Util.changeByteOrderEndianSystem(Util.intToBytes(version)),
                Util.changeByteOrderEndianSystem(hashPrevBlock),
                Util.changeByteOrderEndianSystem(hashMerkleRoot),
                Util.changeByteOrderEndianSystem(Util.longToBytes(timestamp)));

        return Arrays.concatenate(
                tmp,
                Util.changeByteOrderEndianSystem(Util.intToBytes(targetBits)),
                Util.changeByteOrderEndianSystem(Util.intToBytes(nonce)));
    }

    private boolean isValidHash(byte[] hash) {
        // TODO: After make target Adjustable move numberOfBytes and bitsInLastByte Outside this function
        int numberOfBytes = targetBits / 8;
        byte bitsInLastByte = targetBits % 8;

        // Check whole bytes
        for (int i = 0; i < numberOfBytes; i++) {
            if (hash[i] != 0) {
                return false;
            }
        }

        int unsignedLastHashByte = hash[numberOfBytes] & 0xff; // convert to unsigned byte

        // ex: bitsInLastByte = 5 --->  (1 << 8 - 5) = 0000 1000 --> 00001000 = 00000111
        int unsignedTargetInLastByte = (1 << 8 - bitsInLastByte) - 1; // Bitmask

        // unsignedTargetInLastByte : Upper Boundary
        return unsignedLastHashByte <= unsignedTargetInLastByte;
    }
}