package com.atycoin.utility;

import com.google.gson.Gson;
import org.bouncycastle.jcajce.provider.digest.RIPEMD160;
import org.bouncycastle.jcajce.provider.digest.SHA256;

import java.security.MessageDigest;

public class Hash {
    public static byte[] applySHA256(byte[] data) {
        MessageDigest digest = new SHA256.Digest();
        return digest.digest(data);
    }

    public static byte[] doubleSHA256(byte[] data) {
        MessageDigest digest = new SHA256.Digest();
        byte[] firstHash = digest.digest(data);
        return digest.digest(firstHash);
    }

    public static byte[] applyRIPEMP160(byte[] data) {
        MessageDigest digest = new RIPEMD160.Digest();
        return digest.digest(data);
    }

    public static String serialize(byte[] hash) {
        return new Gson().toJson(hash);
    }

    public static byte[] deserialize(String serializedHash) {
        return new Gson().fromJson(serializedHash, byte[].class);
    }
}