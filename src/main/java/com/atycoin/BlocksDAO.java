package com.atycoin;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class BlocksDAO implements Iterable<Block> {
    private static final String TIP_OF_CHAIN_KEY = "l";
    private static final String BEST_HEIGHT_KEY = "h";

    private static BlocksDAO instance;
    private static Jedis dbConnection;

    private BlocksDAO(int port) {
        dbConnection = new Jedis("localhost", port);
    }

    public static BlocksDAO getInstance() {
        if (instance == null) {
            int port = AtycoinStart.getNodeID() + 3379; //shift to simulate multiple node
            instance = new BlocksDAO(port);
        }
        return instance;
    }

    public boolean addBlock(Block block) {
        byte[] previousBlockHash = block.getHashPrevBlock();
        String previousBlockHashSerialized = Util.serializeHash(previousBlockHash);

        String tipOfChain = getTipOfChain();

        if (tipOfChain != null && !previousBlockHashSerialized.equals(tipOfChain)) {
            throw new IllegalArgumentException("Invalid Block.");
        }

        byte[] blockHash = block.getHash();
        String blockHashSerialized = Util.serializeHash(blockHash);
        String blockSerialized = dbConnection.get(blockHashSerialized);

        if (blockSerialized != null) {
            return false;
        }

        Transaction redisTransaction = dbConnection.multi();

        blockSerialized = block.serializeBlock();

        redisTransaction.set(blockHashSerialized, blockSerialized);
        redisTransaction.set(TIP_OF_CHAIN_KEY, blockHashSerialized);
        redisTransaction.incr(BEST_HEIGHT_KEY);

        redisTransaction.exec();
        return true;
    }

    public String getTipOfChain() {
        return dbConnection.get(TIP_OF_CHAIN_KEY);
    }

    public Block getBlock(String blockHash) {
        String blockSerialized = dbConnection.get(blockHash);

        if (blockSerialized == null) {
            throw new NoSuchElementException("Block not found in database.");
        }
        return Block.deserializeBlock(blockSerialized);
    }

    public int getBestHeight() {
        String bestHeight = dbConnection.get(BEST_HEIGHT_KEY);
        if (bestHeight == null) {
            return 0;
        }
        return Integer.parseInt(bestHeight);
    }

    // returns a list of required block hashes from the chain
    public List<String> getBlockHashes(int requiredHeight) {
        List<String> blockHashes = new ArrayList<>();
        for (Block block : this) {
            if (requiredHeight != 0 && block.getHeight() <= requiredHeight) {
                break;
            }

            blockHashes.add(Util.serializeHash(block.getHash()));
        }

        return blockHashes;
    }

    @Override
    public Iterator<Block> iterator() {
        return new BlocksIterator();
    }
}