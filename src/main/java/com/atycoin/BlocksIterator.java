package com.atycoin;

import com.atycoin.utility.Hash;

import java.util.Iterator;

public class BlocksIterator implements Iterator<Block> {
    private static final String GENESIS_PREVIOUS_HASH = Hash.serialize(Constant.EMPTY_BYTE_ARRAY);
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
        currentHashSerialized = Hash.serialize(block.getHashPrevBlock());
        return block;
    }
}