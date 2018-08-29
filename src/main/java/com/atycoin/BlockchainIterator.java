package com.atycoin;

import redis.clients.jedis.Jedis;

import java.util.Iterator;

public class BlockchainIterator implements Iterator<Block> {
    private static String genesisPrevHash = Util.serializeHash(Util.applySha256("Atycoion".getBytes()));
    private Jedis dbConnection;
    private String currentHashSerialized;

    public BlockchainIterator(Jedis dbConnection, String currentHashSerialized) {
        this.dbConnection = dbConnection;
        this.currentHashSerialized = dbConnection.get("l");
    }

    @Override
    public boolean hasNext() {
        if (currentHashSerialized == null)
            return false;

        return !currentHashSerialized.equals(genesisPrevHash);
    }

    @Override
    public Block next() {
        String blockSerialized = dbConnection.get(currentHashSerialized);
        Block block = Util.deserializeBlock(blockSerialized);
        currentHashSerialized = Util.serializeHash(block.hashPrevBlock);
        return block;
    }
}