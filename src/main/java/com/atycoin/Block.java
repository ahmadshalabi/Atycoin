package com.atycoin;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

//TODO: separate some data in BlockHeader
// Block represents a block in the blockchain
public class Block {
    public final int targetBits = 16; //TODO: Make it Adjusted to meet some requirements
    public byte[] hashPrevBlock;
    public byte[] hashMerkleRoot;
    public long timestamp;
    private int version;
    private int nonce;

    public ArrayList<Transaction> transactions;
    public byte[] hash; // Current Block hash

    private Block(ArrayList<Transaction> transactions, byte[] hashPrevBlock) {
        version = 1;
        timestamp = System.currentTimeMillis() / 1000L; // Convert to Second
        this.transactions = transactions;
        this.hashPrevBlock = hashPrevBlock;
        hashMerkleRoot = hashTransactions();
    }

    // newGenesisBlock: creates and returns genesis Block
    public static Block newGenesisBlock(Transaction coinbase) {
        //TODO: Find way to Mining the prevHash of Genesis Block
//        byte[] prevHash = Util.reverseBytesOrder(
//                Util.applySHA256(Util.reverseBytesOrder("Atycoin".getBytes())));

        //add coinbase Transaction
        ArrayList<Transaction> transactions = new ArrayList<>();
        transactions.add(coinbase);

        Block block = new Block(transactions, new byte[0]);

        ProofOfWork proofOfWork = new ProofOfWork(block);
        proofOfWork.runProofOfWork();

        return block;
    }

    // newBlock: creates and returns Block
    public static Block newBlock(ArrayList<Transaction> transactions, byte[] hashPrevBlock) {
        Block block = new Block(transactions, hashPrevBlock);

        ProofOfWork proofOfWork = new ProofOfWork(block);
        proofOfWork.runProofOfWork();

        return block;
    }

    // hashTransactions returns a hash of the transactions in the block
    public byte[] hashTransactions() {
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            for (Transaction transaction : transactions) {
                //little-endian
                buffer.write(Util.reverseBytesOrder(transaction.id));
            }

            //Big-endian
            return Util.reverseBytesOrder(Util.applySHA256(buffer.toByteArray()));
        } catch (IOException e) {
            throw new RuntimeException(e);

        }
    }

    // serializes the block header in bytes form
    public byte[] serializeBlockHeader(int nonce) {
        //TODO: Consider more efficient way to concatenate byte[] arrays

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        try {
            //concatenate data in little-endian order
            buffer.write(Util.reverseBytesOrder(Util.intToBytes(version)));
            buffer.write(Util.reverseBytesOrder(hashPrevBlock));
            buffer.write(Util.reverseBytesOrder(hashMerkleRoot));
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
}