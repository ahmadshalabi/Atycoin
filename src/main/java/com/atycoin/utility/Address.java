package com.atycoin.utility;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

public class Address {
    private static final int CHECKSUM_LEN = 4;
    private static final byte VERSION = 0x00;

    // returns wallet address
    public static String getAddress(byte[] publicKeyHashed) {
        //VersionedPayload
        ByteArrayOutputStream payload = new ByteArrayOutputStream();
        payload.write(VERSION);
        payload.write(publicKeyHashed, 0, publicKeyHashed.length);

        byte[] checksum = checksum(payload.toByteArray());

        //fullPayload
        payload.write(checksum, 0, checksum.length);

        //calc Address base on BASE58 encoding
        return Base58.encode(payload.toByteArray());
    }

    public static byte[] getPublicKeyHashed(String address) {
        byte[] fullPayload = Base58.decode(address);
        return Arrays.copyOfRange(fullPayload, 1, fullPayload.length - 4);
    }

    public static boolean isValidAddress(String address) {
        byte[] fullyPayload = Base58.decode(address);

        int checksumPosition = fullyPayload.length - CHECKSUM_LEN;
        byte[] actualChecksum = Arrays.copyOfRange(fullyPayload, checksumPosition, fullyPayload.length);

        byte[] versionedPayload = Arrays.copyOfRange(fullyPayload, 0, checksumPosition);

        byte[] targetChecksum = checksum(versionedPayload);

        return Arrays.equals(actualChecksum, targetChecksum);
    }

    // generates a checksum for a public key
    private static byte[] checksum(byte[] versionedPayload) {
        byte[] hash = Hash.doubleSHA256(versionedPayload);
        return Arrays.copyOfRange(hash, 0, CHECKSUM_LEN);
    }
}
