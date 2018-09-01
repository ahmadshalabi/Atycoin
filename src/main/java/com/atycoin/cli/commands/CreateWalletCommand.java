package com.atycoin.cli.commands;

import com.atycoin.WalletsProcessor;
import com.atycoin.cli.Commander;

import java.util.Arrays;

public class CreateWalletCommand implements Command {
    @Override
    public String getHelp() {
        return "cmd: createwallet \n" +
                "- description: Generates a new key-pair and saves it into the wallet file \n" +
                "- usage: createwallet param [situational...] \n" +
                "- param: '-help' \n" +
                "------------------------------------------------------------------------";
    }

    @Override
    public String[] getParams() {
        return new String[]{"-help"};
    }

    @Override
    public void run(String[] args) {
        if (args.length == 0) {
            WalletsProcessor walletsProcessor = WalletsProcessor.newWalletsProcessor();
            String address = walletsProcessor.createWallet();
            walletsProcessor.saveToFile();

            System.out.printf("Your new address: %s\n", address);
            return;
        }

        //check if command exists in params list
        if (!Arrays.asList(getParams()).contains(args[0])) {
            Commander.CommanderPrint("ERROR ! unknown parameters...");
            Commander.CommanderPrint(Arrays.toString(getParams()));
            return;
        }

        String[] params = getParams();

        if (args[0].equals(params[0])) { //-help
            Commander.CommanderPrint(getHelp());
        } else {
            Commander.CommanderPrint("Sorry param not yet implemented");
        }
    }
}
