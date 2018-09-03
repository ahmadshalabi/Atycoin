package com.atycoin;

import java.util.ArrayList;

public class MerkleTree {
    MerkleNode rootNode;

    private MerkleTree(MerkleNode rootNode) {
        this.rootNode = rootNode;
    }

    // creates a new Merkle tree from a sequence of data
    public static MerkleTree newMerkleTree(ArrayList<ArrayList<Byte>> data) {
        ArrayList<MerkleNode> nodes = new ArrayList<>();

        int numberOfNodes = data.size();
        if (numberOfNodes % 2 != 0) {
            data.add(data.get(numberOfNodes - 1));
            numberOfNodes++;
        }

        for (ArrayList<Byte> datum : data) {
            MerkleNode node = MerkleNode.newMerkleNode(null, null, Util.listToBytes(datum));
            nodes.add(node);
        }

        for (int i = 0; i < numberOfNodes / 2; i++) {
            ArrayList<MerkleNode> newLevel = new ArrayList<>();

            for (int j = 0, newLevelNodes = nodes.size(); j < newLevelNodes; j += 2) {
                MerkleNode node = MerkleNode.newMerkleNode(nodes.get(j), nodes.get(j + 1), new byte[0]);
                newLevel.add(node);
            }

            nodes = newLevel;
        }

        return new MerkleTree(nodes.get(0));
    }
}
