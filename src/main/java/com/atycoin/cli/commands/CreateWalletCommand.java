package com.atycoin.cli.commands;

import com.atycoin.Wallets;
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
            Wallets wallets = Wallets.getInstance();
            String address = wallets.createWallet();

            Commander.CommanderPrint(String.format("Your new address: %s\n", address));
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