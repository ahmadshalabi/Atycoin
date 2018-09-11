package com.atycoin.utility;

import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.ECPointUtil;
import org.bouncycastle.jce.interfaces.ECPrivateKey;
import org.bouncycastle.jce.interfaces.ECPublicKey;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.util.encoders.Hex;

import java.io.ByteArrayOutputStream;
import java.security.*;
import java.security.spec.*;

public class Keys {
    public static KeyPair generateKeyPair() {
        try {
            ECParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec("secp256k1");
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("ECDSA", "BC");
            keyPairGenerator.initialize(ecSpec, new SecureRandom());
            return keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }
    }

    public static ECPublicKey getPublicKey(byte[] publicKeyEncoded) {
        try {
            X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(publicKeyEncoded);
            KeyFactory keyFactory = KeyFactory.getInstance("ECDSA", "BC");
            return (ECPublicKey) keyFactory.generatePublic(pubKeySpec);
        } catch (NoSuchProviderException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    public static ECPrivateKey getPrivateKey(byte[] privateKeyEncoded) {
        try {
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyEncoded);
            KeyFactory keyFactory = KeyFactory.getInstance("ECDSA", "BC");
            return (ECPrivateKey) keyFactory.generatePrivate(privateKeySpec);
        } catch (NoSuchProviderException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    public static ECPublicKey toPublicKey(byte[] rawPublicKey) {
        // Get RawPublicKey (0x04 + xAffineCoord + yAffineCoord)
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        buffer.write(0x04);
        rawPublicKey = Hex.decode(rawPublicKey);
        buffer.write(rawPublicKey, 0, rawPublicKey.length);

        try {
            ECNamedCurveParameterSpec params = ECNamedCurveTable.getParameterSpec("secp256k1");
            ECCurve curve = params.getCurve();
            EllipticCurve ellipticCurve = EC5Util.convertCurve(curve, params.getSeed());
            ECPoint point = ECPointUtil.decodePoint(ellipticCurve, buffer.toByteArray());
            java.security.spec.ECParameterSpec params2 = EC5Util.convertSpec(ellipticCurve, params);
            ECPublicKeySpec keySpec = new ECPublicKeySpec(point, params2);

            KeyFactory fact = KeyFactory.getInstance("ECDSA", "BC");
            return (ECPublicKey) fact.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException e) {
            throw new RuntimeException();
        }
    }
}