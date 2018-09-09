package com.atycoin.network;

import java.util.ArrayList;
import java.util.List;

public class BlocksInTransit {
    private static final List<String> blocksInTransit = new ArrayList<>();

    public static void addItems(List<String> items) {
        blocksInTransit.addAll(items);
    }

    public static void removeItem(int index) {
        blocksInTransit.remove(index);
    }

    public static int size() {
        return blocksInTransit.size();
    }

    public static String getItem(int index) {
        return blocksInTransit.get(index);
    }
}