package com.atycoin.cli.commands;

import com.atycoin.Block;
import com.atycoin.BlocksDAO;
import com.atycoin.Transaction;
import com.atycoin.Util;
import com.atycoin.cli.Commander;

import java.util.Arrays;
import java.util.List;

public class PrintChainCommand implements Command {
    @Override
    public String getHelp() {
        return "cmd: printchain \n" +
                "- description: print all the blocks of the blockchain. \n" +
                "- usage: printchain param [situational...] \n" +
                "- param: '-help', '-params' \n" +
                "------------------------------------------------------------------------";
    }

    @Override
    public String[] getParams() {
        return new String[]{"-help", "-params"};
    }

    @Override
    public void run(String[] args) {
        if (args.length < 1) { //no parameters
            BlocksDAO blocksDAO = BlocksDAO.getInstance();
            for (Block block : blocksDAO) {
                Commander.CommanderPrint(String.format(
                        "============ Block %s ============", Util.bytesToHex(block.getHash())));
                Commander.CommanderPrint(String.format(
                        "Prev. block: %s", Util.bytesToHex(block.getHashPrevBlock())));
                Commander.CommanderPrint(String.format(
                        "Merkle Root: %s%n", Util.bytesToHex(block.getMerkleRoot())));
                List<Transaction> transactions = block.getTransactions();
                for (Transaction transaction : transactions) {
                    System.out.println(transaction);
                }
            }
            return;
        }

        String[] params = getParams();

        //check if command exists in params list
        if (!Arrays.asList(params).contains(args[0])) {
            Commander.CommanderPrint("ERROR ! unknown parameters...");
            Commander.CommanderPrint(Arrays.toString(params));
            return;
        }

        if (args[0].equals(params[0])) { //help
            Commander.CommanderPrint(getHelp());
        } else if (args[0].equals(params[1])) { //-params
            Commander.CommanderPrint(Arrays.toString(params));
        } else {
            Commander.CommanderPrint("Invalid parameter. Enter 'printchain -help'");
        }
    }
}