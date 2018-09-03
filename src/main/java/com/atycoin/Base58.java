package com.atycoin;

import java.math.BigInteger;

public class Base58 {
    private static final String ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";
    private static final BigInteger BASE58 = new BigInteger(String.valueOf(58));
    private static final int POSITIVE_SIGN = 1;

    // encodes a byte array to Base58
    public static String encode(byte[] fullPayload) {
        if (fullPayload.length == 0) {
            return "";
        }

        StringBuilder address = new StringBuilder();

        // SumOf(in * 2^(i*8))
        BigInteger sumOfDigitBase58 = new BigInteger(POSITIVE_SIGN, fullPayload);

        BigInteger[] quotientAndRemainder;
        while (sumOfDigitBase58.compareTo(BigInteger.ZERO) != 0) {
            quotientAndRemainder = sumOfDigitBase58.divideAndRemainder(BASE58);
            sumOfDigitBase58 = quotientAndRemainder[0]; // quotient
            //Append at beginning of address
            address.insert(0, ALPHABET.charAt(quotientAndRemainder[1].intValue())); // Map Remainder to Alphabet
        }

        //Version byte [Leading Zero]
        if (fullPayload[0] == 0x00) {
            address.insert(0, ALPHABET.charAt(0));
        }

        return address.toString();
    }

    // decodes Base58-encoded data
    public static byte[] decode(String address) {
        if (address.isEmpty()) {
            return new byte[0];
        }

        BigInteger sumOfDigitBase58 = BigInteger.ZERO;

        // sumOfDigitBase58 * 58 + indexOF(digit)
        for (char digit : address.toCharArray()) {
            sumOfDigitBase58 = sumOfDigitBase58.multiply(BASE58);
            sumOfDigitBase58 = sumOfDigitBase58.add(BigInteger.valueOf(ALPHABET.indexOf(digit)));
        }

        // if fullPayloadWithMissingZeros length less than 25, there are leading zero
        byte[] fullPayload = new byte[25];
        byte[] fullPayloadWithMissingZeros = sumOfDigitBase58.toByteArray();
        int numOfLeadingZero = fullPayload.length - fullPayloadWithMissingZeros.length;
        System.arraycopy(fullPayloadWithMissingZeros, 0,
                fullPayload, numOfLeadingZero, fullPayloadWithMissingZeros.length);

        return fullPayload;
    }
}