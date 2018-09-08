package com.atycoin;

import java.io.ByteArrayOutputStream;

public class MerkleNode {
    private byte[] data;
    private MerkleNode left;
    private MerkleNode right;

    // creates a new Merkle tree node
    public static MerkleNode newMerkleNode(MerkleNode left, MerkleNode right, byte[] data) {
        MerkleNode mNode = new MerkleNode();

        if (left == null && right == null) {
            mNode.data = Util.applySHA256(data);
        } else {
            ByteArrayOutputStream prevHashes = new ByteArrayOutputStream();
            if (left != null) {
                prevHashes.write(left.data, 0, left.data.length);
            }
            prevHashes.write(right.data, 0, right.data.length);
            mNode.data = Util.applySHA256(prevHashes.toByteArray());
        }

        mNode.left = left;
        mNode.right = right;

        return mNode;
    }

    public byte[] getData() {
        return data;
    }
}