package com.atycoin;

import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.interfaces.ECPrivateKey;
import org.bouncycastle.jce.interfaces.ECPublicKey;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECPoint;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

// Wallet stores private and public keys
public class Wallet {
    private static final byte VERSION = 0x00;
    private static final int CHECKSUM_LEN = 4;

    private byte[] privateKeyEncoded;
    private byte[] publicKeyEncoded;

    private Wallet() {
    }

    // creates and returns a Wallet
    public static Wallet newWallet() {
        Wallet wallet = new Wallet();
        wallet.generateKeyPair();
        return wallet;
    }

    // hashes public key
    public static byte[] hashPublicKey(byte[] rawPublicKey) {
        byte[] publicSHA256 = Util.applySHA256(rawPublicKey);
        return Util.applyRIPEMP160(publicSHA256);
    }

    // generates a checksum for a public key
    private static byte[] checksum(byte[] versionedPayload) {
        byte[] firstSHA256 = Util.applySHA256(versionedPayload);
        byte[] secondSHA256 = Util.applySHA256(firstSHA256);

        return Arrays.copyOfRange(secondSHA256, 0, CHECKSUM_LEN);
    }

    public static boolean isValidAddress(String address) {
        byte[] fullyPayload = Base58.decode(address);

        int checksumPosition = fullyPayload.length - CHECKSUM_LEN;
        byte[] actualChecksum = Arrays.copyOfRange(fullyPayload, checksumPosition, fullyPayload.length);

        byte[] versionedPayload = Arrays.copyOfRange(fullyPayload, 0, checksumPosition);

        byte[] targetChecksum = checksum(versionedPayload);

        return Arrays.equals(actualChecksum, targetChecksum);
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

    public ECPrivateKey getPrivateKey() {
        try {
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyEncoded);
            KeyFactory keyFactory = KeyFactory.getInstance("ECDSA", "BC");
            return (ECPrivateKey) keyFactory.generatePrivate(privateKeySpec);
        } catch (NoSuchProviderException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
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

    // returns wallet address
    public String getAddress() {
        byte[] publicKeyHash = hashPublicKey(getRawPublicKey());

        //VersionedPayload
        ByteArrayOutputStream payload = new ByteArrayOutputStream();
        payload.write(VERSION);
        payload.write(publicKeyHash, 0, publicKeyHash.length);

        byte[] checksum = checksum(payload.toByteArray());

        //fullPayload
        payload.write(checksum, 0, checksum.length);

        //calc Address base on BASE58 encoding
        return Base58.encode(payload.toByteArray());
    }

    // Get X and Y Coordinate of public key
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
}