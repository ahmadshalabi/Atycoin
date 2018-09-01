package com.atycoin.cli.commands;

import com.atycoin.Block;
import com.atycoin.Blockchain;
import com.atycoin.Util;
import com.atycoin.cli.Commander;

import java.util.Arrays;

public class PrintChainCommand implements Command {
    @Override
    public String getHelp() {
        return "cmd: printchain \n" +
                "- description: print all the blocks of the blockchain. \n" +
                "- usage: printchain param [situational...] \n" +
                "- param: '-help' \n" +
                "------------------------------------------------------------------------";
    }

    @Override
    public String[] getParams() {
        return new String[]{"-help"};
    }

    @Override
    public void run(String[] args) {
        if (args.length == 0) { //no parameters
            for (Block block : Blockchain.getInstance()) {
                System.out.printf("============ Block %s ============%n", Util.bytesToHex(block.hash));
                System.out.printf("Prev. block: %s%n", Util.bytesToHex(block.hashPrevBlock));
                System.out.printf("Merkle Root: %s%n%n", Util.bytesToHex(block.hashMerkleRoot));
            }
            return;
        }

        //check if command exists in params list
        if (!Arrays.asList(getParams()).contains(args[0])) {
            Commander.CommanderPrint("ERROR ! unknown parameters...");
            Commander.CommanderPrint(Arrays.toString(getParams()));
        }

        String[] params = getParams();

        if (args[0].equals(params[0])) { //help
            Commander.CommanderPrint(getHelp());
        } else {
            Commander.CommanderPrint("Sorry param not yet implemented");
        }
    }
}
