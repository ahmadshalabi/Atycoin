package com.atycoin;

import redis.clients.jedis.Jedis;

import java.util.Iterator;

public class Blockchain implements Iterable<Block> {
    private static Blockchain instance;
    private static Jedis dbConnection;
    private String hashOfLastBlockSerialized;


    //TODO: Do manual commit and support rollback (BulkTransaction in DB)
    public Blockchain() {
        dbConnection = new Jedis("localhost");
        hashOfLastBlockSerialized = dbConnection.get("l");

        if (hashOfLastBlockSerialized == null) {
            System.out.println("No existing blockchain found. Creating a new one...\n");
            Block genesisBlock = genesisBlock();
            hashOfLastBlockSerialized = Util.serializeHash(genesisBlock.hash);

            dbConnection.set(hashOfLastBlockSerialized, genesisBlock.serializeBlock());
            dbConnection.set("l", hashOfLastBlockSerialized);
        }
    }

    public static Blockchain getInstance() {
        if (instance == null) {
            instance = new Blockchain();
        }

        return instance;
    }

    public Block genesisBlock() {
        byte[] merkleRoot = "Genesis block".getBytes();
        //TODO: Mining the prevHash
        byte[] prevHash = Util.applySha256("Atycoion".getBytes());

        return new Block(merkleRoot, prevHash);
    }

    public void addBlock(byte[] merkleRoot) {
        hashOfLastBlockSerialized = dbConnection.get("l");
        Block newBlock = new Block(merkleRoot, Util.deserializeHash(hashOfLastBlockSerialized));

        hashOfLastBlockSerialized = Util.serializeHash(newBlock.hash);
        String newBlockSerialized = newBlock.serializeBlock();

        dbConnection.set(hashOfLastBlockSerialized, newBlockSerialized);
        dbConnection.set("l", hashOfLastBlockSerialized);
    }

    @Override
    public Iterator iterator() {
        return new BlockchainIterator(dbConnection, hashOfLastBlockSerialized);
    }
}