package com.atycoin.cli.commands;

import com.atycoin.AtycoinStart;
import com.atycoin.Wallet;
import com.atycoin.cli.Commander;
import com.atycoin.network.Node;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StartNodeCommand implements Command {
    @Override
    public String getHelp() {
        return "cmd: startnode \n" +
                "- description: Start a node with ID specified as environment args to AtycoinStart. -miner enable mining\n" +
                "- usage: startnode param [situational...] \n" +
                "- param: '-help', '-miner ADDRESS' \n" +
                "------------------------------------------------------------------------";
    }

    @Override
    public String[] getParams() {
        return new String[]{"-help", "-miner"};
    }

    @Override
    public void run(String[] args) {
        if (args.length != 0) {
            //check if command exists in params list
            if (!Arrays.asList(getParams()).contains(args[0])) {
                Commander.CommanderPrint("ERROR ! unknown parameters...");
                Commander.CommanderPrint(Arrays.toString(getParams()));
            }
        }

        String[] params = getParams();

        System.out.printf("Starting node %s%n", AtycoinStart.nodeID);

        if (args.length == 0) {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(new Node(AtycoinStart.nodeID, ""));
        } else if (args[0].equals(params[1])) {
            String minerAddress = args[1];

            if (!Wallet.validateAddress(minerAddress)) {
                Commander.CommanderPrint("ERROR: Wrong miner address!");
                return;
            }

            System.out.printf("Mining is on. Address to receive rewards: %s", minerAddress);

            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(new Node(AtycoinStart.nodeID, minerAddress));
        } else if (args[0].equals(params[0])) { //help
            Commander.CommanderPrint(getHelp());
        } else {
            Commander.CommanderPrint("Sorry param not yet implemented");
        }
    }
}