package com.atycoin.cli.commands;

import com.atycoin.AtycoinStart;
import com.atycoin.cli.Commander;
import com.atycoin.network.Node;
import com.atycoin.utility.Address;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StartNodeCommand implements Command {
    private static final ExecutorService pool = Executors.newSingleThreadExecutor();

    @Override
    public String getHelp() {
        return "cmd: startnode \n" +
                "- description: Start a node with ID specified as environment args to AtycoinStart." +
                " -miner enable mining\n" +
                "- usage: startnode param [situational...] \n" +
                "- param: '-help', '-params', -miner ADDRESS' \n" +
                "------------------------------------------------------------------------";
    }

    @Override
    public String[] getParams() {
        return new String[]{"-help", "-params", "-miner"};
    }

    @Override
    public void run(String[] args) {
        String[] params = getParams();

        if (args.length != 0) {
            //check if command exists in params list
            if (!Arrays.asList(params).contains(args[0])) {
                Commander.CommanderPrint("ERROR ! unknown parameters...");
                Commander.CommanderPrint(Arrays.toString(params));
                return;
            }
        }

        System.out.printf("Starting node %s%n", AtycoinStart.getNodeID());

        if (args.length < 1) {
            pool.execute(Node.getInstance(""));
        } else if (args[0].equals(params[2])) {
            String minerAddress = args[1];

            if (!Address.isValidAddress(minerAddress)) {
                Commander.CommanderPrint("ERROR: Wrong miner address!");
                return;
            }

            pool.execute(Node.getInstance(minerAddress));
            System.out.printf("Mining is on. Address to receive rewards: %s", minerAddress);
        } else if (args[0].equals(params[0])) { //help
            Commander.CommanderPrint(getHelp());
        } else if (args[0].equals(params[1])) { //-params
            Commander.CommanderPrint(Arrays.toString(params));
        } else {
            Commander.CommanderPrint("Invalid parameter. Enter 'startnode -help'");
        }
    }
}