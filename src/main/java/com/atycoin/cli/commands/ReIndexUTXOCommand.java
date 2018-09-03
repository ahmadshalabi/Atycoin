package com.atycoin.cli.commands;

import com.atycoin.UTXOSet;
import com.atycoin.cli.Commander;

import java.util.Arrays;

public class ReIndexUTXOCommand implements Command {
    @Override
    public String getHelp() {
        return "cmd: reindexutxo \n" +
                "- description: Rebuilds the UTXO set. \n" +
                "- usage: reindexutxo param [situational...] \n" +
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
            UTXOSet utxoSet = UTXOSet.getInstance();
            utxoSet.reIndex();

            Commander.CommanderPrint(String.format("Done! There are %d transactions in the UTXO set.\n", utxoSet.countTransactions()));
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
