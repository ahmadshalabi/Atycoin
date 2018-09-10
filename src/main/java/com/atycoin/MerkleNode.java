package com.atycoin;

import com.atycoin.utility.Hash;

import java.io.ByteArrayOutputStream;

public class MerkleNode {
    private byte[] data;
    private MerkleNode left;
    private MerkleNode right;

    // creates a new Merkle tree node
    public static MerkleNode newMerkleNode(MerkleNode left, MerkleNode right, byte[] data) {
        MerkleNode mNode = new MerkleNode();

        if (left == null && right == null) {
            mNode.data = Hash.applySHA256(data);
        } else {
            ByteArrayOutputStream previousHashes = new ByteArrayOutputStream();
            if (left != null) {
                byte[] leftData = left.data;
                previousHashes.write(left.data, 0, leftData.length);
            }

            byte[] rightData = right.data;
            previousHashes.write(right.data, 0, rightData.length);
            byte[] previousHashesBytes = previousHashes.toByteArray();
            mNode.data = Hash.applySHA256(previousHashesBytes);
        }

        mNode.left = left;
        mNode.right = right;

        return mNode;
    }

    public byte[] getData() {
        return data;
    }
}