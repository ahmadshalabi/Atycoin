package com.atycoin.utility;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class Bytes {
    public static String toHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(); // This will contain hash as hexadecimal
        for (byte element : hash) {
            String hex = Integer.toHexString(0xff & element);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static byte[] toBytes(long input) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(Long.BYTES);
        byteBuffer.putLong(input);
        return byteBuffer.array();
    }

    public static byte[] toBytes(int input) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(Integer.BYTES);
        byteBuffer.putInt(input);
        return byteBuffer.array();
    }

    public static byte[] toBytes(List<Byte> list) {
        int listLength = list.size();
        byte[] data = new byte[listLength];
        for (int i = 0; i < listLength - 1; i++) {
            data[i] = list.get(i);
        }
        return data;
    }

    public static List<Byte> toByteList(byte[] data) {
        List<Byte> list = new ArrayList<>();
        for (byte element : data) {
            list.add(element);
        }
        return list;
    }
}