package com.atycoin;

import com.atycoin.utility.Bytes;
import com.atycoin.utility.Constant;
import com.atycoin.utility.Hash;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Block {
    private transient static final int GENESIS_HEIGHT = 0;

    private final int version;
    private final byte[] hashPrevBlock;
    private final long timestamp;
    private final int targetBits = 5; //TODO: Make it Adjusted to meet some requirements
    private final int height;
    private final List<Transaction> transactions;

    private byte[] merkleRoot;
    private int nonce;
    private byte[] hash;

    private Block(List<Transaction> transactions, byte[] hashPrevBlock, int height) {
        version = 1;
        timestamp = System.currentTimeMillis() / 1000L; // Convert to Second
        this.transactions = transactions;
        this.hashPrevBlock = hashPrevBlock;
        this.height = height;
    }

    // creates and returns genesis Block
    public static Block newGenesisBlock(Transaction coinbase) {
        //add coinbase Transaction
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(coinbase);

        return newBlock(transactions, Constant.EMPTY_BYTE_ARRAY, GENESIS_HEIGHT);
    }

    public static Block newBlock(List<Transaction> transactions, byte[] hashPrevBlock, int height) {
        Block block = new Block(transactions, hashPrevBlock, height);
        block.setRemainingData();

        return block;
    }

    public static Block deserializeBlock(String serializedBlock) {
        Gson decoder = new Gson();
        return decoder.fromJson(serializedBlock, Block.class);
    }

    public String serializeBlock() {
        Gson encoder = new Gson();
        return encoder.toJson(this);
    }

    public byte[] setBlockHeader(int nonce) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        try {
            buffer.write(Bytes.toBytes(version));
            buffer.write(hashPrevBlock);
            buffer.write(merkleRoot);
            buffer.write(Bytes.toBytes(timestamp));
            buffer.write(Bytes.toBytes(targetBits));
            buffer.write(Bytes.toBytes(nonce));

            return buffer.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] getHashPrevBlock() {
        return hashPrevBlock;
    }

    public byte[] getMerkleRoot() {
        return merkleRoot;
    }

    public int getTargetBits() {
        return targetBits;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public byte[] getHash() {
        return hash;
    }

    public int getHeight() {
        return height;
    }

    private void setRemainingData() {
        setMerkleRoot();
        setNonce();
        setHash();
    }

    private void setMerkleRoot() {
        MerkleTree mTree = MerkleTree.newMerkleTree(getTransactionsHashes());
        merkleRoot = mTree.getMerkleRoot();
    }

    private List<List<Byte>> getTransactionsHashes() {
        List<List<Byte>> transactionIDs = new ArrayList<>();
        for (Transaction transaction : transactions) {
            byte[] id = transaction.getId();
            List<Byte> transactionID = Bytes.toByteList(id);
            transactionIDs.add(transactionID);
        }
        return transactionIDs;
    }

    private void setNonce() {
        ProofOfWork proofOfWork = new ProofOfWork(this);
        nonce = proofOfWork.runProofOfWork();
    }

    private void setHash() {
        byte[] blockHeader = setBlockHeader(nonce);
        hash = Hash.doubleSHA256(blockHeader);
        System.out.println(Bytes.toHex(hash));
    }
}