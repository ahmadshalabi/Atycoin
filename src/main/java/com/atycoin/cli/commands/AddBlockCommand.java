package com.atycoin.cli.commands;

import com.atycoin.cli.Commander;

import java.util.Arrays;

public class AddBlockCommand implements Command {
    @Override
    public String getHelp() {
        return "cmd: addblock \n" +
                "- description: add a block to the blockchain \n" +
                "- usage: addblock -data BLOCK_DATA \n" +
                "- param: '-help', '-data' \n" +
                "------------------------------------------------------------------------";
    }

    @Override
    public String[] getParams() {
        return new String[]{"-help", "-data"};
    }

    @Override
    public void run(String[] args) {
        //check if command exists in params list
        if (!Arrays.asList(getParams()).contains(args[0])) {
            Commander.CommanderPrint("ERROR ! unknown parameters...");
            Commander.CommanderPrint(Arrays.toString((getParams())));
        }

        String[] params = getParams();

        if (args[0].equals(params[1])) { //data
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                if (i != 1)
                    stringBuilder.append(" ");
                stringBuilder.append(args[i]);
            }

            //Blockchain.getInstance().addBlock(stringBuilder.toString());
            Commander.CommanderPrint("Success!");
        } else if (args[0].equals(params[0])) { //help
            Commander.CommanderPrint(getHelp());
        } else {
            Commander.CommanderPrint("Sorry param not yet implemented");
        }
    }
}
