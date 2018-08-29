package com.atycoin;

import com.atycoin.cli.Commander;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import redis.clients.jedis.Jedis;

import java.security.Security;

public class AtycoinStart {
    public static void main(String[] args) {
        //Setup bouncey castle as security provider
        Security.addProvider(new BouncyCastleProvider());

        //Connect to Redis database
        Jedis dbConnection = new Jedis("localhost");

        Blockchain blockchain = new Blockchain();

//        blockchain.addBlock("Send 1 BTC to Ivan".getBytes());
//        blockchain.addBlock("Send 2 more BTC to Ivan".getBytes());

        Commander commander = new Commander();
        commander.listen();

        for (Block block : blockchain) {
            System.out.printf("Prev. hash: %s%n", Util.bytesToHex(block.hashPrevBlock));
            System.out.printf("Data: %s%n", Util.bytesToHex(block.hashMerkleRoot));
            System.out.printf("Hash: %s%n", Util.bytesToHex(block.hash));
            //    System.out.printf("PoW: %b%n", block.isValidBlock());
            System.out.println();
        }
    }
}