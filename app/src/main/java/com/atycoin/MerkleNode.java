package com.atycoin;

import com.atycoin.utility.Hash;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MerkleNode node = (MerkleNode) o;
        return Arrays.equals(data, node.data) &&
                Objects.equals(left, node.left) &&
                Objects.equals(right, node.right);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(left, right);
        result = 31 * result + Arrays.hashCode(data);
        return result;
    }

    @Override
    public String toString() {
        return new StringJoiner("\n")
                .add("data=" + Arrays.toString(data))
                .add("left=" + left)
                .add("right=" + right)
                .toString();
    }
}