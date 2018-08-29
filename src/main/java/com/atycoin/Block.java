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

        ProofOfWork.getInstance(block).runProofOfWork();

        return block;
    }

    // newBlock: creates and returns Block
    public static Block newBlock(ArrayList<Transaction> transactions, byte[] hashPrevBlock) {
        Block block = new Block(transactions, hashPrevBlock);
        ProofOfWork.getInstance(block).runProofOfWork();
        return block;
    }

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

    // Serialize the block
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