package com.atycoin.cli.commands;

import com.atycoin.Blockchain;
import com.atycoin.Wallet;
import com.atycoin.cli.Commander;

import java.util.Arrays;

public class CreateBlockchainCommand implements Command {
    @Override
    public String getHelp() {
        return "cmd: createblockchain \n" +
                "- description: Create a blockchain and send genesis block reward to ADDRESS \n" +
                "- usage: createblockchain param [situational...] \n" +
                "- param: '-address ADDRESS', '-help'\n" +
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
            } else if (!Wallet.validateAddress(address)) {
                Commander.CommanderPrint("ERROR ! Address in not valid.");
                return;
            }

            Blockchain blockchain = Blockchain.getInstance();
            blockchain.createBlockchain(address);
        } else if (args[0].equals(params[0])) { //-help
            Commander.CommanderPrint(getHelp());
        } else {
            Commander.CommanderPrint("Sorry param not yet implemented");
        }
    }
}