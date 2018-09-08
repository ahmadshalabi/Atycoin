package com.atycoin;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

// Block represents a block in the blockchain
public class Block {
    private final int targetBits = 16; //TODO: Make it Adjusted to meet some requirements
    private byte[] hashPrevBlock;
    private byte[] merkleRoot;
    private long timestamp;
    private int version;
    private int nonce;
    private int height;

    private ArrayList<Transaction> transactions;
    private byte[] hash; // Current Block hash

    private Block(ArrayList<Transaction> transactions, byte[] hashPrevBlock, int height) {
        version = 1;
        timestamp = System.currentTimeMillis() / 1000L; // Convert to Second
        this.transactions = transactions;
        this.hashPrevBlock = hashPrevBlock;
        this.height = height;
        merkleRoot = hashTransactions();
    }

    // newGenesisBlock: creates and returns genesis Block
    public static Block newGenesisBlock(Transaction coinbase) {
        //add coinbase Transaction
        ArrayList<Transaction> transactions = new ArrayList<>();
        transactions.add(coinbase);

        Block block = new Block(transactions, new byte[0], 0);

        ProofOfWork proofOfWork = new ProofOfWork(block);
        proofOfWork.runProofOfWork();

        return block;
    }

    // newBlock: creates and returns Block
    public static Block newBlock(ArrayList<Transaction> transactions, byte[] hashPrevBlock, int height) {
        Block block = new Block(transactions, hashPrevBlock, height);

        ProofOfWork proofOfWork = new ProofOfWork(block);
        proofOfWork.runProofOfWork();

        return block;
    }

    // hashTransactions returns a hash of the transactions in the block
    public byte[] hashTransactions() {
        ArrayList<ArrayList<Byte>> transactions = new ArrayList<>();
        for (Transaction transaction : this.transactions) {
            //little-endian
            ArrayList<Byte> transactionId = Util.BytesToList(Util.reverseBytesOrder(transaction.id));
            transactions.add(transactionId);
        }

        MerkleTree mTree = MerkleTree.newMerkleTree(transactions);
        MerkleNode rootNode = mTree.getRootNode();
        return rootNode.getData();
    }

    // serializes the block header in bytes form
    public byte[] serializeBlockHeader(int nonce) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        try {
            //concatenate data in little-endian order
            buffer.write(Util.reverseBytesOrder(Util.intToBytes(version)));
            buffer.write(Util.reverseBytesOrder(hashPrevBlock));
            buffer.write(Util.reverseBytesOrder(merkleRoot));
            buffer.write(Util.reverseBytesOrder(Util.longToBytes(timestamp)));
            buffer.write(Util.reverseBytesOrder(Util.intToBytes(targetBits)));
            buffer.write(Util.reverseBytesOrder(Util.intToBytes(nonce)));

            return buffer.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Block deserializeBlock(String serializedBlock) {
        Gson decoder = new Gson();
        return decoder.fromJson(serializedBlock, Block.class);
    }

    // Serialize the block
    public String serializeBlock() {
        Gson encoder = new Gson();

        return encoder.toJson(this);
    }

    public void setHash(byte[] hash) {
        this.hash = hash;
    }

    public void setNonce(int nonce) {
        this.nonce = nonce;
    }

    public int getHeight() {
        return height;
    }

    public byte[] getHash() {
        return hash;
    }

    public int getTargetBits() {
        return targetBits;
    }

    public byte[] getHashPrevBlock() {
        return hashPrevBlock;
    }

    public byte[] getMerkleRoot() {
        return merkleRoot;
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }
}