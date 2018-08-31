package com.atycoin;

import redis.clients.jedis.Jedis;

import java.util.Iterator;

public class BlockchainIterator implements Iterator<Block> {
    // get Big-endian form for genesisPrevHash and serialize it
    private static final String genesisPrevHash = Util.serializeHash(Util.reverseBytesOrder(
            Util.applySHA256(Util.reverseBytesOrder("Atycoin".getBytes()))));

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
    //returns next block starting from the tip
    @Override
    public Block next() {
        String blockSerialized = dbConnection.get(currentHashSerialized);
        Block block = Block.deserializeBlock(blockSerialized);

        // get next blockHash
        currentHashSerialized = Util.serializeHash(block.hashPrevBlock);
        return block;
    }
}