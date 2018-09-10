package com.atycoin;

import com.atycoin.utility.Bytes;
import com.atycoin.utility.Hash;

public class ProofOfWork {
    private final Block block;
    private final int targetBytes;
    private final int lastByteTarget;
    private byte[] blockHeader;

    public ProofOfWork(Block block) {
        this.block = block;

        //calculate target
        int targetBits = block.getTargetBits();
        targetBytes = targetBits / 8;
        int lastTargetBits = targetBits % 8;
        lastByteTarget = ((1 << 8 - lastTargetBits) - 1) & 0xff;
    }

    //TODO: Add extraNonce when nonce overflow occurs
    public int runProofOfWork() {
        //initialize blockHeader
        int nonce = 0;
        blockHeader = block.setBlockHeader(nonce);
        while (nonce < Integer.MAX_VALUE) {
            updateBlockHeader(nonce);

            byte[] hash = Hash.doubleSHA256(blockHeader);
            if (isHashMeetTarget(hash)) {
                break;
            }
            nonce++;
        }
        return nonce;
    }

    private void updateBlockHeader(int nonce) {
        byte[] nonceBytes = Bytes.reverseOrder(Bytes.toBytes(nonce));
        //update the last 4 bytes
        System.arraycopy(nonceBytes, 0, blockHeader, blockHeader.length - 4, 4);
    }

    private boolean isHashMeetTarget(byte[] hash) {
        int index;
        int targetIndex = hash.length - targetBytes;
        for (index = hash.length - 1; index >= targetIndex; index--) {
            if (hash[index] != 0) {
                return false;
            }
        }

        int lastByteInHash = hash[index];
        return lastByteInHash <= lastByteTarget;
    }
}