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
            MerkleNode node = MerkleNode.newMerkleNode(null, null, Util.listToBytes(datum));
            nodes.add(node);
        }

        int numberOfNodes = nodes.size();
        while (numberOfNodes > 1) {
            List<MerkleNode> newLevel = new ArrayList<>();
            for (int leftNode = 0, rightNode = leftNode + 1; leftNode < numberOfNodes; leftNode += 2) {
                if (rightNode == numberOfNodes) {
                    rightNode = leftNode;
                }

                MerkleNode node = MerkleNode.newMerkleNode(nodes.get(leftNode), nodes.get(rightNode), new byte[0]);
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