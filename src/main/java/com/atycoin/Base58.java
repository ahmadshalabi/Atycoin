package com.atycoin;

import java.math.BigInteger;

public class Base58 {
    private static final char[] ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz".toCharArray();
    private static final String ALPHABET_String = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";
    private static final BigInteger BASE58 = new BigInteger(String.valueOf(ALPHABET.length));
    private static final int POSITIVE_SIGNUM = 1;

    // encodes a byte array to Base58
    public static byte[] encode(byte[] input) {
        if (input.length == 0) {
            return new byte[0];
        }

        StringBuilder buffer = new StringBuilder();

        // SumOf(in * 2^(i*8)
        BigInteger sumOfInputInBase58 = new BigInteger(POSITIVE_SIGNUM, input);

        BigInteger[] quotientAndRemainder;
        while (sumOfInputInBase58.compareTo(BigInteger.ZERO) != 0) {
            quotientAndRemainder = sumOfInputInBase58.divideAndRemainder(BASE58);
            sumOfInputInBase58 = quotientAndRemainder[0]; // quotient
            //Append at beginning of buffer
            buffer.insert(0, ALPHABET[quotientAndRemainder[1].intValue()]); // Map Remainder to Alphabet
        }

        // Convert leading zeros
        for (byte digit : input) {
            if (digit == 0x00) {
                buffer.insert(0, ALPHABET[0]); // 0x00 in BASE58 == '1'
            } else {
                break;
            }
        }

        return buffer.toString().getBytes();
    }

    // decodes Base58-encoded data
    public static byte[] decode(byte[] input) {

        if (input.length == 0) {
            return new byte[0];
        }

        BigInteger sum = BigInteger.ZERO;

        // sum * 58 + indexOF(digit)
        for (byte digit : input) {
            sum = sum.multiply(BASE58);

            sum = sum.add(BigInteger.valueOf(ALPHABET_String.indexOf(digit)));
        }

        // if incompleteFullPayload length less than 25, there are leading zero
        byte[] fullPayload = new byte[25];
        byte[] incompleteFullPayload = sum.toByteArray();
        int numOfLeadingZero = fullPayload.length - incompleteFullPayload.length;
        System.arraycopy(incompleteFullPayload, 0, fullPayload, numOfLeadingZero, incompleteFullPayload.length);

        return fullPayload;
    }
}