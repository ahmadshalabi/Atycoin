package com.atycoin;

import org.bouncycastle.jcajce.provider.asymmetric.ec.KeyPairGeneratorSpi;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECPoint;
import java.util.Arrays;

// Wallet stores private and public keys
public class Wallet {
    static final byte VERSION = 0x00;
    static final int CHECKSUM_LEN = 4;

    ECPrivateKey privateKey;
    byte[] publicKey;

    private Wallet() {
    }

    // creates and returns a Wallet
    public static Wallet newWallet() {
        Wallet wallet = new Wallet();
        wallet.generateKeyPair();

        return wallet;
    }

    public static void main(String[] args) {
        Wallet wallet = newWallet();

        byte[] address = wallet.getAddress();
        System.out.println(wallet.validateAddress(address));
    }

    private void generateKeyPair() {
        try {
            KeyPairGenerator keyGen = new KeyPairGeneratorSpi.ECDSA();
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp256k1");

            // Initialize the key generator and generate a KeyPair
            keyGen.initialize(ecSpec, random);

            //Generate private and public keys
            KeyPair keyPair = keyGen.generateKeyPair();

            // Get ECPrivateKey from keyPair
            privateKey = (ECPrivateKey) keyPair.getPrivate();

            //Get ECPublicKey form keyPair
            ECPublicKey ecPublicKey = (ECPublicKey) keyPair.getPublic();

            //Get X and Y Coordinate of ecPublicKey
            ECPoint ecPoint = ecPublicKey.getW();
            BigInteger xCoordinate = ecPoint.getAffineX();
            BigInteger yCoordinate = ecPoint.getAffineY();

            // concatenate X and Y
            String buffer = xCoordinate.toString(16) +
                    yCoordinate.toString(16);

            // publicKey in byte[] form
            publicKey = buffer.getBytes();

        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }
    }

    // returns wallet address
    public byte[] getAddress() {
        byte[] publicKeyHash = hashPublicKey();

        //VersionedPayload
        ByteArrayOutputStream payload = new ByteArrayOutputStream();
        payload.write(VERSION);
        payload.write(publicKeyHash, 0, publicKeyHash.length);

        byte[] checksum = checksum(payload.toByteArray());

        //fullPayload
        payload.write(checksum, 0, checksum.length);

        return Base58.encode(payload.toByteArray());
    }

    // hashes public key
    public byte[] hashPublicKey() {
        byte[] publicSHA256 = Util.applySHA256(publicKey);
        return Util.applayRIPEMP160(publicSHA256);
    }

    // generates a checksum for a public key
    public byte[] checksum(byte[] payload) {
        byte[] firstSHA = Util.applySHA256(payload);
        byte[] secondSHA = Util.applySHA256(firstSHA);

        return Arrays.copyOfRange(secondSHA, 0, CHECKSUM_LEN);
    }

    public boolean validateAddress(byte[] address) {
        byte[] fullyPayload = Base58.decode(address);

        int checksumPos = fullyPayload.length - CHECKSUM_LEN;
        byte[] actualChecksum = Arrays.copyOfRange(fullyPayload, checksumPos, fullyPayload.length);

        byte[] versionedPayload = Arrays.copyOfRange(fullyPayload,
                0, checksumPos);

        byte[] targetChecksum = checksum(versionedPayload);

        return Arrays.equals(actualChecksum, targetChecksum);
    }
}
