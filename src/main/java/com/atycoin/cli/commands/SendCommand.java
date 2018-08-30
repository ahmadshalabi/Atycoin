package com.atycoin.cli.commands;

import com.atycoin.Blockchain;
import com.atycoin.Transaction;
import com.atycoin.cli.Commander;

import java.util.ArrayList;
import java.util.Arrays;

public class SendCommand implements Command {
    @Override
    public String getHelp() {
        return "cmd: send \n" +
                "- description: Send AMOUNT of coins FROM address to TO \n" +
                "- usage: send param [situational...] \n" +
                "- param: '-help', '-from FROM -to TO -amount' \n" +
                "------------------------------------------------------------------------";
    }

    @Override
    public String[] getParams() {
        return new String[]{"-help", "-from"};
    }

    @Override
    public void run(String[] args) {
        //check if command exists in params list
        if (!Arrays.asList(getParams()).contains(args[0])) {
            Commander.CommanderPrint("ERROR ! unknown parameters...");
            Commander.CommanderPrint(Arrays.toString(getParams()));
        }

        String[] params = getParams();

        if (args[0].equals(params[1]) && args[2].equals("-to") && args[4].equals("-amount")) { //-from FROM -to TO -amount AMOUNT
            String from = args[1];
            String to = args[3];
            int amount = Integer.parseInt(args[5]);

            Transaction newUTXOTransaction = Transaction.newUTXOTransaction(from, to, amount);

            if (newUTXOTransaction == null) {
                return;
            }

            ArrayList<Transaction> transactions = new ArrayList<>();
            transactions.add(newUTXOTransaction);

            Blockchain.getInstance().mineBlock(transactions);
            Commander.CommanderPrint("Success!");
        } else if (args[0].equals(params[0])) { //help
            Commander.CommanderPrint(getHelp());
        } else {
            Commander.CommanderPrint("Sorry param not yet implemented");
        }
    }
}
