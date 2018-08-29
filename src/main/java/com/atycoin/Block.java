package com.atycoin;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Block {
    public final int targetBits = 8; //TODO: Make it Adjusted to meet some requirements
    public byte[] hashPrevBlock;
    public byte[] hashMerkleRoot;
    public long timestamp;
    public ArrayList<Transaction> transactions;
    //TODO: separate relevant hashMerkleRoot in BlockHeader
    private int version = 1;
    private int nonce;
    public byte[] hash; // Current Block hash

    private Block(ArrayList<Transaction> transactions, byte[] hashPrevBlock) {
        timestamp = System.currentTimeMillis() / 1000L; // Convert to Second
        this.transactions = transactions;
        this.hashPrevBlock = hashPrevBlock;

        hashMerkleRoot = hashTransaction();
    }

    // newGenesisBlock: creates and returns genesis Block
    public static Block newGenesisBlock(Transaction coinbase) {
        //byte[] merkleRoot = "Genesis block".getBytes();
        //TODO: Mining the prevHash
        byte[] prevHash = Util.applySha256("Atycoion".getBytes());

        //add coinbase Transaction
        ArrayList<Transaction> transactions = new ArrayList<>();
        transactions.add(coinbase);

        Block block = new Block(transactions, prevHash);

        ProofOfWork.getInstance(block).proofOfWork();

        return block;
    }

    // newBlock: creates and returns Block
    public static Block newBlock(ArrayList<Transaction> transactions, byte[] hashPrevBlock) {
        Block block = new Block(transactions, hashPrevBlock);
        ProofOfWork.getInstance(block).proofOfWork();
        return block;
    }

//    public void proofOfWork() {
//        System.out.printf("Mining the block containing \"%s\"\n", new String(hashMerkleRoot));
//
//        //TODO: Enhance checking in nonce overflow
//        while (nonce < Integer.MAX_VALUE) {
//            byte[] data = concatenateBlockData();
//
//            // Double hash
//            hash = Util.applySha256(Util.applySha256(data));
//
//            // Change to Big-endian
//            hash = Util.changeByteOrderEndianSystem(hash);
//
//            if (isValidHash(hash)) {
//                break;
//            }
//
//            nonce++;
//        }
//        System.out.println(Util.bytesToHex(hash));
//        System.out.println();
//    }

//    // TODO: Clean isValidProofOFWork
//    public boolean isValidBlock() {
//        byte[] data = concatenateBlockData();
//
//        byte[] calculatedHash = Util.applySha256(Util.applySha256(data));
//
//        calculatedHash = Util.changeByteOrderEndianSystem(calculatedHash);
//
//        if (!isValidHash(calculatedHash)) {
//            return false;
//        }
//
//        return Arrays.equals(hash, calculatedHash);
//    }

    public byte[] concatenateBlockData() {
        //TODO: Consider more efficient way to concatenate byte[] arrays

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            buffer.write(Util.changeByteOrderEndianSystem(Util.intToBytes(version)));
            buffer.write(Util.changeByteOrderEndianSystem(hashPrevBlock));
            buffer.write(Util.changeByteOrderEndianSystem(hashMerkleRoot));
            buffer.write(Util.changeByteOrderEndianSystem(Util.longToBytes(timestamp)));
            buffer.write(Util.changeByteOrderEndianSystem(Util.intToBytes(targetBits)));
            buffer.write(Util.changeByteOrderEndianSystem(Util.intToBytes(nonce)));

            return buffer.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // hashTransactions returns a hash of the transactions in the block
    public byte[] hashTransaction() {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        for (Transaction transaction : transactions) {
            buffer.write(transaction.transactionId, 0, transaction.transactionId.length);
        }

        return Util.applySha256(buffer.toByteArray());
    }

//    private boolean isValidHash(byte[] hash) {
//        // TODO: After make target Adjustable move numberOfBytes and bitsInLastByte Outside this function
//        int numberOfBytes = targetBits / 8;
//        byte bitsInLastByte = targetBits % 8;
//
//        // Check whole bytes
//        for (int i = 0; i < numberOfBytes; i++) {
//            if (hash[i] != 0) {
//                return false;
//            }
//        }
//
//        int unsignedLastHashByte = hash[numberOfBytes] & 0xff; // convert to unsigned byte
//
//        // ex: bitsInLastByte = 5 --->  (1 << 8 - 5) = 0000 1000 --> 00001000 = 00000111
//        int unsignedTargetInLastByte = (1 << 8 - bitsInLastByte) - 1; // Bitmask
//
//        // unsignedTargetInLastByte : Upper Boundary
//        return unsignedLastHashByte <= unsignedTargetInLastByte;
//    }

    public String serializeBlock() {
        Gson gson = new Gson();

        return gson.toJson(this);
    }

    public void setHash(byte[] hash) {
        this.hash = hash;
    }

    public void setNonce(int nonce) {
        this.nonce = nonce;
    }
}