package com.atycoin.network;

import java.util.ArrayList;
import java.util.List;

public class KnownNodes {
    private static List<Integer> knownNodes; // To simulate DNS Seed

    static {
        knownNodes = new ArrayList<>();
        knownNodes.add(3000);
    }

    public static int get(int index) {
        return knownNodes.get(index);
    }

    public static List<Integer> getKnownNodes() {
        return knownNodes;
    }

    public static int size() {
        return knownNodes.size();
    }

    public static void add(int address) {
        if (!knownNodes.contains(address)) {
            knownNodes.add(address);
        }
    }
}
