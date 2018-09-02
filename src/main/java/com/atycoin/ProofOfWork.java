package com.atycoin;

public class ProofOfWork {
    private Block block;
    private int targetBits;
    private byte[] hash;
    private int nonce;

    public ProofOfWork(Block block) {
        this.block = block;
        targetBits = block.targetBits;
    }

    // performs a proof-of-work
    public void runProofOfWork() {
        System.out.println("Mining a new block");

        //TODO: Enhance checking in nonce overflow
        while (nonce < Integer.MAX_VALUE) {
            byte[] blockData = this.block.serializeBlockHeader(nonce);

            // Double hash
            hash = Util.applySHA256(Util.applySHA256(blockData));

            // Change to Big-endian
            hash = Util.reverseBytesOrder(hash);

            if (isHashMeetTarget(hash)) {
                break;
            }

            nonce++;
        }

        block.setHash(hash);
        block.setNonce(nonce);

        System.out.println(Util.bytesToHex(hash));
        System.out.println();
    }

    private boolean isHashMeetTarget(byte[] hash) {
        // TODO: After make target Adjustable move numberOfBytes and bitsInLastByte Outside this function
        int numberOfBytes = targetBits / 8;
        int bitsInLastByte = targetBits % 8;

        // Check whole bytes
        for (int i = 0; i < numberOfBytes; i++) {
            if (hash[i] != 0) {
                return false;
            }
        }

        int unsignedLastHashByte = hash[numberOfBytes] & 0xff; // convert to unsigned byte

        // ex: bitsInLastByte = 5 --->  (1 << 8 - 5) = 0000 1000 --> 00001000 = 00000111
        int unsignedTargetInLastByte = ((1 << 8 - bitsInLastByte) - 1) & 0xff; // Bitmask

        // unsignedTargetInLastByte : Upper Boundary
        return unsignedLastHashByte <= unsignedTargetInLastByte;
    }

    // TODO: Clean isValidProofOFWork
    // validates block's PoW
    public boolean isValidProofOFWork() {
        byte[] blockData = block.serializeBlockHeader(nonce);

        byte[] calculatedHash = Util.applySHA256(Util.applySHA256(blockData));

        // Change to Big-endian
        calculatedHash = Util.reverseBytesOrder(calculatedHash);

        return isHashMeetTarget(calculatedHash);
    }
}