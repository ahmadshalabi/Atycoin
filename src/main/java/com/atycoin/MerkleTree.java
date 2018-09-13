package com.atycoin;

import com.atycoin.utility.Bytes;
import com.atycoin.utility.Constant;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

public class MerkleTree {
    private final MerkleNode rootNode;

    private MerkleTree(MerkleNode rootNode) {
        this.rootNode = rootNode;
    }

    // creates a new Merkle tree from a sequence of data
    public static MerkleTree newMerkleTree(List<List<Byte>> data) {
        List<MerkleNode> nodes = new ArrayList<>();

        for (List<Byte> datum : data) {
            byte[] datumBytes = Bytes.toBytes(datum);
            MerkleNode node = MerkleNode.newMerkleNode(null, null, datumBytes);
            nodes.add(node);
        }

        int numberOfNodes = nodes.size();
        while (numberOfNodes > 1) {
            List<MerkleNode> newLevel = new ArrayList<>();
            for (int left = 0, right = left + 1; left < numberOfNodes; left += 2) {
                if (right == numberOfNodes) {
                    right = left;
                }

                MerkleNode leftNode = nodes.get(left);
                MerkleNode rightNode = nodes.get(right);

                MerkleNode node = MerkleNode.newMerkleNode(leftNode, rightNode, Constant.EMPTY_BYTE_ARRAY);

                newLevel.add(node);
            }
            nodes = newLevel;
            numberOfNodes = nodes.size();
        }

        return new MerkleTree(nodes.get(0));
    }

    public byte[] getMerkleRoot() {
        return rootNode.getData();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MerkleTree that = (MerkleTree) o;
        return Objects.equals(rootNode, that.rootNode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rootNode);
    }

    @Override
    public String toString() {
        return new StringJoiner("\n")
                .add("rootNode=" + rootNode)
                .toString();
    }
}