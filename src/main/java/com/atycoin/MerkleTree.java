package com.atycoin;

import java.util.ArrayList;
import java.util.List;

public class MerkleTree {
    private final MerkleNode rootNode;

    private MerkleTree(MerkleNode rootNode) {
        this.rootNode = rootNode;
    }

    // creates a new Merkle tree from a sequence of data
    public static MerkleTree newMerkleTree(List<List<Byte>> data) {
        List<MerkleNode> nodes = new ArrayList<>();

        for (List<Byte> datum : data) {
            byte[] datumBytes = Util.listToBytes(datum);
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
}