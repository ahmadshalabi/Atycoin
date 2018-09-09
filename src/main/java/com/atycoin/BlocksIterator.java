package com.atycoin;

import java.util.Iterator;

public class BlocksIterator implements Iterator<Block> {
    private static final String GENESIS_PREVIOUS_HASH =
            Util.serializeHash(Constant.EMPTY_BYTE_ARRAY);

    private final BlocksDAO blocksDAO;
    private String currentHashSerialized;

    public BlocksIterator() {
        blocksDAO = BlocksDAO.getInstance();
        currentHashSerialized = blocksDAO.getTipOfChain();
    }

    @Override
    public boolean hasNext() {
        if (currentHashSerialized == null) {
            return false;
        }
        return !currentHashSerialized.equals(GENESIS_PREVIOUS_HASH);
    }

    @Override
    public Block next() {
        Block block = blocksDAO.getBlock(currentHashSerialized);

        // get next blockHash
        currentHashSerialized = Util.serializeHash(block.getHashPrevBlock());
        return block;
    }
}