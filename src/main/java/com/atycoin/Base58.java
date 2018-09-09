package com.atycoin;

import java.math.BigInteger;

public class Base58 {
    private static final String ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";
    private static final String EMPTY_ADDRESS = "";
    private static final BigInteger BASE58 = new BigInteger(String.valueOf(58));
    private static final int POSITIVE_SIGN = 1;

    // encodes a byte array to Base58
    public static String encode(byte[] fullPayload) {
        if (fullPayload.length == 0) {
            return EMPTY_ADDRESS;
        }

        // SumOf(in * 2^(i*8))
        BigInteger sumOfDigitBase58 = new BigInteger(POSITIVE_SIGN, fullPayload);

        StringBuilder address = new StringBuilder();
        BigInteger[] quotientAndRemainder;
        while (sumOfDigitBase58.compareTo(BigInteger.ZERO) != 0) {
            quotientAndRemainder = sumOfDigitBase58.divideAndRemainder(BASE58);
            sumOfDigitBase58 = quotientAndRemainder[0]; // quotient
            BigInteger remainder = quotientAndRemainder[1];
            int alphabetIndex = remainder.intValue();

            //Insert at beginning of address
            address.insert(0, ALPHABET.charAt(alphabetIndex)); // Map Remainder to Alphabet
        }

        //Version byte [Leading Zero]
        if (fullPayload[0] == 0) {
            address.insert(0, ALPHABET.charAt(0));
        }

        return address.toString();
    }

    // decodes Base58-encoded data
    public static byte[] decode(String address) {
        if (address.isEmpty()) {
            return Constant.EMPTY_BYTE_ARRAY;
        }

        BigInteger sumOfDigits = BigInteger.ZERO;

        // sumOfDigits * 58 + indexOf(digit)
        char[] addressDigits = address.toCharArray();
        for (char digit : addressDigits) {
            sumOfDigits = sumOfDigits.multiply(BASE58);

            int index = ALPHABET.indexOf(digit);
            BigInteger valueOfIndex = BigInteger.valueOf(index);
            sumOfDigits = sumOfDigits.add(valueOfIndex);
        }

        // if fullPayloadWithMissingZeros length less than 25, there are leading zero
        byte[] fullPayload = new byte[25];
        byte[] fullPayloadWithMissingZeros = sumOfDigits.toByteArray();
        int numberOfLeadingZero = fullPayload.length - fullPayloadWithMissingZeros.length;

        System.arraycopy(fullPayloadWithMissingZeros, 0,
                fullPayload, numberOfLeadingZero, fullPayloadWithMissingZeros.length);

        return fullPayload;
    }
}