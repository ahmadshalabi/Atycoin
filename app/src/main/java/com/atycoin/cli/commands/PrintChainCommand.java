package com.atycoin.cli.commands;

import com.atycoin.Block;
import com.atycoin.cli.Commander;
import com.atycoin.database.BlocksDAO;

import java.util.Arrays;

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
                System.out.println(block);
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