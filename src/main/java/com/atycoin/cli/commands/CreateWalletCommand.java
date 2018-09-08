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
                "- param: '-help', '-params' \n" +
                "------------------------------------------------------------------------";
    }

    @Override
    public String[] getParams() {
        return new String[]{"-help", "-params"};
    }

    @Override
    public void run(String[] args) {
        if (args.length < 1) {
            Wallets wallets = Wallets.getInstance();
            String address = wallets.createWallet();

            Commander.CommanderPrint(String.format("Your new address: %s\n", address));
            return;
        }

        String[] params = getParams();

        //check if command exists in params list
        if (!Arrays.asList(params).contains(args[0])) {
            Commander.CommanderPrint("ERROR ! unknown parameters...");
            Commander.CommanderPrint(Arrays.toString(params));
            return;
        }

        if (args[0].equals(params[0])) { //-help
            Commander.CommanderPrint(getHelp());
        } else if (args[0].equals(params[1])) { //-params
            Commander.CommanderPrint(Arrays.toString(params));
        } else {
            Commander.CommanderPrint("Invalid parameter. Enter 'createwallet -help'");
        }
    }
}