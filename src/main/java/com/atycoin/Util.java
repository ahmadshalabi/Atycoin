package com.atycoin;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.ECGenParameterSpec;

public class Util {
    public static void main(String[] args) {
        String msg = "Ahmad";
        byte[] msgHashed = hash(msg);
        System.out.print("Message hash: ");
        System.out.println(toHexString(msgHashed));
        System.out.println();

        KeyPair keyPair = generateKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();

        System.out.print("Private key: ");
        System.out.println(toHexString(privateKey.getEncoded()));
        System.out.print("Encoded Private Key: ");
        System.out.println();

        System.out.print("Public key: ");
        System.out.println(toHexString(publicKey.getEncoded()));
        System.out.print("Encoded Public key: ");
        System.out.println();

        byte[] signature = createSignature(privateKey, msgHashed);
        System.out.print("Message Signature: ");
        System.out.println(toHexString(signature));

        boolean bool = verifySignature(publicKey, msgHashed, signature);

        if (bool) {
            System.out.println("Signature verified");
        } else {
            System.out.println("Signature failed");
        }
    }

    public static byte[] hash(String message) {
        byte[] digest = {0};
        try {
            //Creating the MessageDigest object
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            //Passing data to the created MessageDigest Object
            md.update(message.getBytes(StandardCharsets.UTF_8));

            //Compute the message digest
            digest = md.digest();

            return digest;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return digest;
    }

    public static String toHexString(byte[] hashValue) {
        //Converting the byte array in to HexString format
        StringBuffer hexString = new StringBuffer();

        for (int i = 0; i < hashValue.length; i++) {
            String hex = Integer.toHexString(0xff & hashValue[i]);
            if (hex.length() == 1)
                hexString.append('0');
            hexString.append(hex); // 0xFF & ff ff ff fe = fe
        }

        return hexString.toString();
    }

    public static KeyPair generateKeyPair() {

        KeyPair keyPair = null;
        try {
            //Creating KeyPair generator object
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC");


            //Initializing the KeyPairGenerator
            ECGenParameterSpec ecGenParameterSpec = new ECGenParameterSpec("secp256k1");
            SecureRandom secureRandom = new SecureRandom();
            keyPairGenerator.initialize(ecGenParameterSpec, secureRandom);

            keyPair = keyPairGenerator.generateKeyPair();
            return keyPair;
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

        return keyPair;
    }

    public static byte[] createSignature(PrivateKey privateKey, byte[] data) {
        byte[] digitalSignature = {0};
        try {
            //Creating a Signature object
            Signature signature = Signature.getInstance("SHA256withECDSA");


            //Initialize the signature
            SecureRandom secureRandom = new SecureRandom();
            signature.initSign(privateKey, secureRandom);

            //Adding data to the signature
            signature.update(data);

            //Calculating the signature
            digitalSignature = signature.sign();
            return digitalSignature;

        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
        }

        return digitalSignature;
    }

    public static boolean verifySignature(PublicKey publicKey, byte[] data, byte[] signatureToVerified) {
        try {
            //Creating a Signature object
            Signature signature = Signature.getInstance("SHA256withECDSA");

            //Initializing the signature
            signature.initVerify(publicKey);

            //Update the data to be verified
            signature.update(data);

            //Verifying the signature
            return signature.verify(signatureToVerified);
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
        }

        return false;
    }
}
