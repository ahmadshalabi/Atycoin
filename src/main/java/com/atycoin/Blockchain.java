package com.atycoin;

import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.Iterator;

//Blockchain implements interaction with a DB
public class Blockchain implements Iterable<Block> {
    private static final String genesisCoinbaseData = "The Times 03/Jan/2009 Chancellor on brink of second bailout for banks";

    private static Blockchain instance = new Blockchain();

    private static Jedis dbConnection;
    private String tipOfChain;

    //TODO: Do manual commit and support rollback (BulkTransaction in DB)
    private Blockchain() {
        dbConnection = new Jedis("localhost");
        tipOfChain = dbConnection.get("l");
    }

    public static Blockchain getInstance() {
        return instance;
    }

    //TODO: Check filed connection
    public void addBlock(ArrayList<Transaction> transactions) {
        tipOfChain = dbConnection.get("l"); //To store it as prevBlockHash in the new block

        Block newBlock = Block.newBlock(transactions, Util.deserializeHash(tipOfChain));
        //Block newBlock = new Block(transactions, Util.deserializeHash(tipOfChain));

        tipOfChain = Util.serializeHash(newBlock.hash);
        String newBlockSerialized = newBlock.serializeBlock();

        //Store new block int database and update hash of last block
        dbConnection.set(tipOfChain, newBlockSerialized);
        dbConnection.set("l", tipOfChain);
    }

    //TODO: Check filed connection
    // creates a new Blockchain with genesis Block
    public void createBlockchain(String address) {
        tipOfChain = dbConnection.get("l");

        if (tipOfChain == null) {
            System.out.println("No existing blockchain found. Creating a new one...\n");

            Transaction newCoinbaseTX = Transaction.newCoinbaseTX(address, genesisCoinbaseData);
            Block genesisBlock = Block.newGenesisBlock(newCoinbaseTX);

            tipOfChain = Util.serializeHash(genesisBlock.hash);

            dbConnection.set(tipOfChain, genesisBlock.serializeBlock());
            dbConnection.set("l", tipOfChain);

            System.out.println("Done!");
        } else {
            System.out.println("Blockchain already exists.");
        }
    }

    //iterator return BlockchainIterator Used to iterate over blockchain blocks
    @Override
    public Iterator<Block> iterator() {
        return new BlockchainIterator(dbConnection);
    }
}