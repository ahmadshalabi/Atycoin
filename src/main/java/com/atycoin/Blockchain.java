package com.atycoin;

import com.atycoin.database.BlocksDAO;
import com.atycoin.utility.Hash;

import java.util.List;

public class Blockchain {
    private static Blockchain instance;
    private BlocksDAO blocksDAO;

    private Blockchain() {
        blocksDAO = BlocksDAO.getInstance();
    }

    public static Blockchain getInstance() {
        if (instance == null) {
            instance = new Blockchain();
        }
        return instance;
    }

    // creates a new Blockchain with genesis Block
    public void createBlockchain(String address) {
        String tipOfChain = blocksDAO.getTipOfChain();

        if (tipOfChain == null) {
            System.out.println("No existing blockchain found. Creating a new one...\n");

            Transaction coinbaseTransaction = Transaction.newCoinbaseTransaction(address);
            Block genesisBlock = Block.newGenesisBlock(coinbaseTransaction);
            addBlock(genesisBlock);

            System.out.println("Done!");
        } else {
            System.out.println("Blockchain already exists.");
        }
    }

    public Block mineBlock(List<Transaction> transactions) {
        String tipOfChain = blocksDAO.getTipOfChain();
        byte[] previousBlockHash = Hash.deserialize(tipOfChain);

        int newHeight = blocksDAO.getBestHeight() + 1;

        Block newBlock = Block.newBlock(transactions, previousBlockHash, newHeight);
        addBlock(newBlock);

        return newBlock;
    }

    private void addBlock(Block newBlock) {
        blocksDAO.addBlock(newBlock);
        ChainState.getInstance().update(newBlock);
    }
}