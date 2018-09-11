package com.atycoin;

import com.atycoin.utility.Address;
import com.atycoin.utility.Hash;
import com.atycoin.utility.Keys;
import org.bouncycastle.jce.interfaces.ECPrivateKey;
import org.bouncycastle.jce.interfaces.ECPublicKey;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECPoint;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;

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
        return Keys.getPrivateKey(privateKeyEncoded);
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
        KeyPair keyPair = Keys.generateKeyPair();
        privateKeyEncoded = keyPair.getPrivate().getEncoded();
        publicKeyEncoded = keyPair.getPublic().getEncoded();
    }

    private ECPublicKey getPublicKey() {
        return Keys.getPublicKey(publicKeyEncoded);
    }
}