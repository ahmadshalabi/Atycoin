package com.atycoin;

import com.atycoin.cli.Commander;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;

public class AtycoinStart {
    public static void main(String[] args) {
        //Setup bouncy castle as security provider
        Security.addProvider(new BouncyCastleProvider());

        Commander.getInstance().listen();
    }
}