package com.atycoin;

//TODO: Clean Utility class

import com.google.gson.Gson;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.jcajce.provider.digest.RIPEMD160;
import org.bouncycastle.jcajce.provider.digest.SHA256;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.ECPointUtil;
import org.bouncycastle.jce.interfaces.ECPrivateKey;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.security.*;
import java.security.interfaces.ECPublicKey;
import java.security.spec.*;
import java.util.ArrayList;
import java.util.List;

public class Util {
    public static byte[] applySHA256(byte[] data) {
        MessageDigest digest = new SHA256.Digest();

        //Applies sha256 to our data
        return digest.digest(data);
    }

    public static byte[] applyRIPEMP160(byte[] data) {
        MessageDigest digest = new RIPEMD160.Digest();

        return digest.digest(data);
    }

    // Applies ECDSA Signature and returns the result (as bytes)
    public static byte[] applyECDSASig(ECPrivateKey privateKey, byte[] data) {
        try {
            Signature signature = Signature.getInstance("ECDSA", "BC");
            signature.initSign(privateKey);
            signature.update(data);
            return signature.sign();
        } catch (NoSuchAlgorithmException | SignatureException | InvalidKeyException | NoSuchProviderException e) {
            throw new RuntimeException(e);
        }
    }

    //Verifies a String signature
    public static boolean verifyECDSASig(ECPublicKey publicKey, byte[] data, byte[] signature) {
        try {
            Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");
            ecdsaVerify.initVerify(publicKey);
            ecdsaVerify.update(data);
            return ecdsaVerify.verify(signature);
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeyException | SignatureException e) {
            throw new RuntimeException(e);
        }
    }

    public static ECPublicKey decodeKey(byte[] encoded) {

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        // Get RawPublicKey (0x04 + xAffineCoord + yAffineCoord)
        buffer.write(0x04);
        encoded = Hex.decode(encoded);
        buffer.write(encoded, 0, encoded.length);

        try {
            ECNamedCurveParameterSpec params = ECNamedCurveTable.getParameterSpec("secp256k1");

            ECCurve curve = params.getCurve();
            EllipticCurve ellipticCurve = EC5Util.convertCurve(curve, params.getSeed());
            ECPoint point = ECPointUtil.decodePoint(ellipticCurve, buffer.toByteArray());
            ECParameterSpec params2 = EC5Util.convertSpec(ellipticCurve, params);
            ECPublicKeySpec keySpec = new ECPublicKeySpec(point, params2);

            KeyFactory fact = KeyFactory.getInstance("ECDSA", "BC");
            return (ECPublicKey) fact.generatePublic(keySpec);

        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException e) {
            throw new RuntimeException();
        }
    }


    // Just for printing on CLI
    public static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(); // This will contain hash as hexadecimal
        for (byte element : hash) {
            String hex = Integer.toHexString(0xff & element);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
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

    public static byte[] listToBytes(List<Byte> list) {
        int listLength = list.size();
        byte[] data = new byte[listLength];
        for (int i = 0; i < listLength - 1; i++) {
            data[i] = list.get(i);
        }

        return data;
    }

    public static List<Byte> BytesToList(byte[] data) {
        List<Byte> list = new ArrayList<>();
        for (byte element : data) {
            list.add(element);
        }

        return list;
    }

    // Convert between Endian Order
    public static byte[] reverseBytesOrder(byte[] data) {
        return Arrays.reverse(data);
    }

    // To store and retrieve form Map and Database
    public static String serializeHash(byte[] hash) {
        return new Gson().toJson(hash);
    }

    public static byte[] deserializeHash(String serializedHash) {
        return new Gson().fromJson(serializedHash, byte[].class);
    }
}