package com.atycoin.cli.commands;

import com.atycoin.Blockchain;
import com.atycoin.TransactionOutput;
import com.atycoin.cli.Commander;

import java.util.ArrayList;
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
        return new String[]{"-help", "-address"};
    }

    @Override
    public void run(String[] args) {
        //check if command exists in params list
        if (!Arrays.asList(getParams()).contains(args[0])) {
            Commander.CommanderPrint("ERROR ! unknown parameters...");
            Commander.CommanderPrint(Arrays.toString(getParams()));
            return;
        }

        String[] params = getParams();

        if (args[0].equals(params[1])) { //-address
            String address = args[1];
            if (address == null) {
                Commander.CommanderPrint("ERROR ! no address entered.");
                return;
            } else if (args.length > 2) {
                Commander.CommanderPrint("ERROR ! Invalid address entered.");
                return;
            }

            Blockchain blockchain = Blockchain.getInstance();
            ArrayList<TransactionOutput> UTXOs = blockchain.findUTXO(address);

            int balance = 0;
            for (TransactionOutput transactionOutput : UTXOs) {
                balance += transactionOutput.value;
            }

            System.out.printf("Balance of '%s': %d\n", address, balance);
        } else if (args[0].equals(params[0])) { //-help
            Commander.CommanderPrint(getHelp());
        } else {
            Commander.CommanderPrint("Sorry param not yet implemented");
        }
    }
}