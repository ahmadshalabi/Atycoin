package com.atycoin;

public class ProofOfWork {
    private final Block block;
    private final int targetBits;
    private byte[] blockHeader;

    public ProofOfWork(Block block) {
        this.block = block;
        targetBits = block.getTargetBits();
    }

    public int runProofOfWork() {
        //initialize blockHeader
        int nonce = 0;
        blockHeader = block.setBlockHeader(nonce);

        //TODO: Add extraNonce when nonce overflow occurs
        while (nonce < Integer.MAX_VALUE) {
            updateBlockHeader(nonce++);

            // Double hash
            byte[] hash = Util.applySHA256(Util.applySHA256(blockHeader));

            // Change to Big-endian
            hash = Util.reverseBytesOrder(hash);

            if (isHashMeetTarget(hash)) {
                break;
            }
        }
        return nonce;
    }

    private void updateBlockHeader(int nonce) {
        byte[] nonceBytes = Util.reverseBytesOrder(Util.intToBytes(nonce));
        //update the last 4 bytes
        System.arraycopy(nonceBytes, 0, blockHeader, blockHeader.length - 4, 4);
    }

    private boolean isHashMeetTarget(byte[] hash) {
        // TODO: After make target Adjustable move numberOfBytes and bitsInLastByte Outside this function
        int numberOfBytes = targetBits / 8;
        int bitsInLastByte = targetBits % 8;

        // Check whole bytes
        for (int byteIndex = 0; byteIndex < numberOfBytes; byteIndex++) {
            if (hash[byteIndex] != 0) {
                return false;
            }
        }

        int unsignedLastHashByte = hash[numberOfBytes] & 0xff; // convert to unsigned byte

        // ex: bitsInLastByte = 5 --->  (1 << 8 - 5) = 0000 1000 --> 00001000 = 00000111
        int unsignedTargetInLastByte = ((1 << 8 - bitsInLastByte) - 1) & 0xff; // Bitmask

        // unsignedTargetInLastByte : Upper Boundary
        return unsignedLastHashByte <= unsignedTargetInLastByte;
    }
}