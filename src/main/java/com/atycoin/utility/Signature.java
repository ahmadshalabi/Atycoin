package com.atycoin.utility;

import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.ECPointUtil;
import org.bouncycastle.jce.interfaces.ECPrivateKey;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.util.encoders.Hex;

import java.io.ByteArrayOutputStream;
import java.security.*;
import java.security.interfaces.ECPublicKey;
import java.security.spec.*;

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
            ECPublicKey publicKey = getPublicKey(rawPublicKey);
            ecdsaVerify.initVerify(publicKey);
            ecdsaVerify.update(data);
            return ecdsaVerify.verify(signature);
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeyException | SignatureException e) {
            throw new RuntimeException(e);
        }
    }

    private static ECPublicKey getPublicKey(byte[] rawPublicKey) {
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
            ECParameterSpec params2 = EC5Util.convertSpec(ellipticCurve, params);
            ECPublicKeySpec keySpec = new ECPublicKeySpec(point, params2);

            KeyFactory fact = KeyFactory.getInstance("ECDSA", "BC");
            return (ECPublicKey) fact.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException e) {
            throw new RuntimeException();
        }
    }
}