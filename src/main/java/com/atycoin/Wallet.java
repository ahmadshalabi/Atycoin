package com.atycoin;

import com.atycoin.utility.Address;
import com.atycoin.utility.Hash;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.interfaces.ECPrivateKey;
import org.bouncycastle.jce.interfaces.ECPublicKey;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECPoint;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

// Wallet stores private and public keys
public class Wallet {
    private byte[] privateKeyEncoded;
    private byte[] publicKeyEncoded;

    private Wallet() {
    }

    public static Wallet newWallet() {
        Wallet wallet = new Wallet();
        wallet.generateKeyPair();
        return wallet;
    }

    public ECPrivateKey getPrivateKey() {
        try {
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyEncoded);
            KeyFactory keyFactory = KeyFactory.getInstance("ECDSA", "BC");
            return (ECPrivateKey) keyFactory.generatePrivate(privateKeySpec);
        } catch (NoSuchProviderException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    public String getAddress() {
        byte[] publicKeyHashed = getPublicKeyHashed();
        return Address.getAddress(publicKeyHashed);
    }

    public byte[] getPublicKeyHashed() {
        byte[] rawPublicKey = getRawPublicKey();
        byte[] HashSHA256 = Hash.applySHA256(rawPublicKey);
        return Hash.applyRIPEMP160(HashSHA256);
    }

    public byte[] getRawPublicKey() {
        //Get ECPublicKey form keyPair
        ECPublicKey ecPublicKey = getPublicKey();

        //Get X and Y Coordinate of ecPublicKey
        ECPoint pointBefore = ecPublicKey.getQ();
        ECFieldElement affineXCoordination = pointBefore.getAffineXCoord();
        ECFieldElement affineYCoordination = pointBefore.getAffineYCoord();

        // concatenate X and Y
        String pubKeyBufferBefore = affineXCoordination.toString() + affineYCoordination.toString();

        // rawPublicKey in byte[] form
        return pubKeyBufferBefore.getBytes(StandardCharsets.UTF_8);
    }

    private void generateKeyPair() {
        try {
            ECParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec("secp256k1");
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("ECDSA", "BC");
            keyPairGenerator.initialize(ecSpec, new SecureRandom());
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            privateKeyEncoded = keyPair.getPrivate().getEncoded();
            publicKeyEncoded = keyPair.getPublic().getEncoded();
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
    }

    private ECPublicKey getPublicKey() {
        try {
            X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(publicKeyEncoded);
            KeyFactory keyFactory = KeyFactory.getInstance("ECDSA", "BC");
            return (ECPublicKey) keyFactory.generatePublic(pubKeySpec);
        } catch (NoSuchProviderException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }
}