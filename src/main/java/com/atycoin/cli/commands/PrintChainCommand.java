package com.atycoin.cli.commands;

import com.atycoin.Block;
import com.atycoin.Blockchain;
import com.atycoin.Util;
import com.atycoin.cli.Commander;

import java.util.Arrays;

public class PrintChainCommand implements Command {
    @Override
    public String getHelp() {
        return "cmd: addblock \n" +
                "- description: Add new block to the chains. \n" +
                "- usage: addblock param [situational...] \n" +
                "- param: '-help' \n" +
                "------------------------------------------------------------------------";
    }

    @Override
    public String[] getParams() {
        return new String[]{"-help"};
    }

    @Override
    public void run(String[] args) {
        //if No arguments
        if (args.length == 0) {
            for (Block block : Blockchain.getInstance()) {
                System.out.printf("Prev. hash: %s%n", Util.bytesToHex(block.hashPrevBlock));
                System.out.printf("Data: %s%n", Util.bytesToString(block.hashMerkleRoot));
                System.out.printf("Hash: %s%n", Util.bytesToHex(block.hash));
                System.out.printf("PoW: %b%n%n", block.isValidBlock());
            }
            return;
        }

        //check if command exists in params list
        if (!Arrays.asList(getParams()).contains(args[0])) {
            Commander.CommanderPrint("ERROR ! unknown parameters...");
            Commander.CommanderPrint(Arrays.toString((getParams())));
        }

        String[] params = getParams();

        if (args[0].equals(params[0])) { //help
            Commander.CommanderPrint(getHelp());
        } else {
            Commander.CommanderPrint("Sorry param not yet implemented");
        }
    }
}
