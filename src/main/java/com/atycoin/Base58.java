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
        BigInteger sumOfInputInBase8 = new BigInteger(POSITIVE_SIGNUM, input);

        BigInteger[] quotientAndRemainder;
        while (sumOfInputInBase8.compareTo(BigInteger.ZERO) != 0) {
            quotientAndRemainder = sumOfInputInBase8.divideAndRemainder(BASE58);
            sumOfInputInBase8 = quotientAndRemainder[0]; // quotient
            //Append at beginning of buffer
            buffer.insert(0, ALPHABET[quotientAndRemainder[1].intValue()]); // Map Remainder to Alphabet
        }

        // Convert leading zeros
        for (byte element : input) {
            if (element == 0x00) {
                buffer.insert(0, ALPHABET[0]);
            } else {
                break;
            }
        }

        return buffer.toString().getBytes();
    }

    // decodes Base58-encoded data
    public static byte[] decode(byte[] input) {
        BigInteger num = BigInteger.ZERO;

        for (byte elem : input) {
            num = num.multiply(BASE58);

            num = num.add(BigInteger.valueOf(ALPHABET_String.indexOf(elem)));
        }

        // if numBytes length less than 25, there are leading zero
        byte[] result = new byte[25];
        byte[] numBytes = num.toByteArray();
        int numOfLeadingZero = result.length - numBytes.length;
        System.arraycopy(numBytes, 0, result, numOfLeadingZero, numBytes.length);

        return result;
    }
}