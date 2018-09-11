package com.atycoin.utility;

import org.bouncycastle.jce.interfaces.ECPrivateKey;
import org.bouncycastle.jce.interfaces.ECPublicKey;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;

public class Signature {
    public static byte[] sign(ECPrivateKey privateKey, byte[] data) {
        try {
            java.security.Signature signature = java.security.Signature.getInstance("ECDSA", "BC");
            signature.initSign(privateKey);
            signature.update(data);
            return signature.sign();
        } catch (NoSuchAlgorithmException | SignatureException | InvalidKeyException | NoSuchProviderException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean verify(byte[] rawPublicKey, byte[] data, byte[] signature) {
        try {
            java.security.Signature ecdsaVerify = java.security.Signature.getInstance("ECDSA", "BC");
            ECPublicKey publicKey = Keys.toPublicKey(rawPublicKey);
            ecdsaVerify.initVerify(publicKey);
            ecdsaVerify.update(data);
            return ecdsaVerify.verify(signature);
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeyException | SignatureException e) {
            throw new RuntimeException(e);
        }
    }
}