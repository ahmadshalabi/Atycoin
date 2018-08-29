package com.atycoin;

//TODO: BASE58 Encoding
//TODO: Convert key to Address
//TODO: Checksum
//TODO: Clean Utility class

import com.google.gson.Gson;
import org.bouncycastle.util.Arrays;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;

public class Util {
    public static byte[] applySha256(byte[] input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256", "BC");

            //Applies sha256 to our input
            return digest.digest(input);
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException(e);
        }
    }

    // Applies ECDSA Signature and returns the result (as bytes)
    public static byte[] applyECDSASig(PrivateKey privateKey, byte[] input) {
        try {
            Signature dsa = Signature.getInstance("ECDSA", "BC");
            dsa.initSign(privateKey);
            dsa.update(input);
            return dsa.sign();
        } catch (NoSuchAlgorithmException | SignatureException | InvalidKeyException | NoSuchProviderException e) {
            throw new RuntimeException(e);
        }
    }

    //Verifies a String signature
    public static boolean verifyECDSASig(PublicKey publicKey, byte[] data, byte[] signature) {
        try {
            Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");
            ecdsaVerify.initVerify(publicKey);
            ecdsaVerify.update(data);
            return ecdsaVerify.verify(signature);
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeyException | SignatureException e) {
            throw new RuntimeException();
        }
    }

    public static String getStringFromKey(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(); // This will contain hash as hexidecimal
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static byte[] stringToBytes(String input) {
        return input.getBytes(StandardCharsets.UTF_8);
    }

    public static byte[] longToBytes(long input) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(Long.BYTES);
        byteBuffer.putLong(input);
        return byteBuffer.array();
    }

    public static byte[] intToBytes(int input) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(Integer.BYTES);
        byteBuffer.putInt(input);
        return byteBuffer.array();
    }

    public static byte[] changeByteOrderEndianSystem(byte[] input) {
        return Arrays.reverse(input);
    }

    public static Block deserializeBlock(String serializedBlock) {
        Gson gson = new Gson();
        return gson.fromJson(serializedBlock, Block.class);
    }

    // Short hand helper to turn Object into a json string
    public static String serializeHash(byte[] hash) {
        return new Gson().toJson(hash);
    }

    public static byte[] deserializeHash(String serializedHash) {
        return new Gson().fromJson(serializedHash, byte[].class);
    }

    public static String bytesToString(byte[] input) {
        return new String(input);
    }
}