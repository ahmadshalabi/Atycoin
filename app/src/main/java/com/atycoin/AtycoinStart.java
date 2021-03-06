package com.atycoin;

import com.atycoin.cli.Commander;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;

public class AtycoinStart {
    private static int nodeID = 0;

    public static void main(String[] args) {
        //Setup bouncy castle as security provider
        Security.addProvider(new BouncyCastleProvider());

        if (args.length == 1)
            nodeID = Integer.parseInt(args[0]);
        System.out.printf("NodeID: %d%n", nodeID);

        Commander.getInstance().listen();
    }

    public static int getNodeID() {
        return nodeID;
    }
}