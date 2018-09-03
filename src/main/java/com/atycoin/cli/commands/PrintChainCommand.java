package com.atycoin.cli.commands;

import com.atycoin.Block;
import com.atycoin.Blockchain;
import com.atycoin.Transaction;
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
                Commander.CommanderPrint(String.format(
                        "============ Block %s ============", Util.bytesToHex(block.hash)));
                Commander.CommanderPrint(String.format(
                        "Prev. block: %s", Util.bytesToHex(block.hashPrevBlock)));
                Commander.CommanderPrint(String.format(
                        "Merkle Root: %s%n", Util.bytesToHex(block.hashMerkleRoot)));
                for (Transaction transaction : block.transactions) {
                    System.out.println(transaction);
                }
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
