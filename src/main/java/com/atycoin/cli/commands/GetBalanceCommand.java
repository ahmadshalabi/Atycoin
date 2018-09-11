package com.atycoin.cli.commands;

import com.atycoin.Base58;
import com.atycoin.ChainState;
import com.atycoin.Wallet;
import com.atycoin.cli.Commander;

import java.util.Arrays;

public class GetBalanceCommand implements Command {
    @Override
    public String getHelp() {
        return "cmd: getbalance \n" +
                "- description: Get balance of ADDRESS \n" +
                "- usage: getbalance param [situational...] \n" +
                "- param: '-address' ADDRESS, '-help' \n" +
                "------------------------------------------------------------------------";
    }

    @Override
    public String[] getParams() {
        return new String[]{"-help", "-params", "-address"};
    }

    @Override
    public void run(String[] args) {
        String[] params = getParams();

        //check if command exists in params list
        if (!Arrays.asList(params).contains(args[0])) {
            Commander.CommanderPrint("ERROR ! unknown parameters...");
            Commander.CommanderPrint(Arrays.toString(params));
            return;
        }

        if (args[0].equals(params[2])) { //-address
            String address = args[1];
            if (address == null) {
                Commander.CommanderPrint("ERROR ! no address entered.");
                return;
            } else if (args.length > 2) {
                Commander.CommanderPrint("ERROR ! Invalid address entered.");
                return;
            } else if (!Wallet.isValidAddress(address)) {
                Commander.CommanderPrint("ERROR ! Address in not valid.");
                return;
            }

            byte[] fullPayload = Base58.decode(address);
            byte[] publicKeyHashed = Arrays.copyOfRange(fullPayload, 1, fullPayload.length - 4);
            int balance = ChainState.getInstance().getBalance(publicKeyHashed);

            System.out.printf("Balance of '%s': %d\n", address, balance);
        } else if (args[0].equals(params[0])) { //-help
            Commander.CommanderPrint(getHelp());
        } else if (args[0].equals(params[1])) { //-params
            Commander.CommanderPrint(Arrays.toString(params));
        } else {
            Commander.CommanderPrint("Invalid parameter. Enter 'getbalance -help'");
        }
    }
}