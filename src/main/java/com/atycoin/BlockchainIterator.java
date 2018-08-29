package com.atycoin;

import redis.clients.jedis.Jedis;

import java.util.Iterator;

public class BlockchainIterator implements Iterator<Block> {
    private static String genesisPrevHash = Util.serializeHash(Util.applySha256("Atycoion".getBytes()));
    private Jedis dbConnection;
    private String currentHashSerialized;

    //TODO: Check filed connection
    public BlockchainIterator(Jedis dbConnection) {
        this.dbConnection = dbConnection;
        currentHashSerialized = dbConnection.get("l");
    }

    @Override
    public boolean hasNext() {
        if (currentHashSerialized == null)
            return false;

        return !currentHashSerialized.equals(genesisPrevHash);
    }

    //TODO: Check filed connection
    @Override
    public Block next() {
        String blockSerialized = dbConnection.get(currentHashSerialized);
        Block block = Util.deserializeBlock(blockSerialized);
        currentHashSerialized = Util.serializeHash(block.hashPrevBlock);
        return block;
    }
}