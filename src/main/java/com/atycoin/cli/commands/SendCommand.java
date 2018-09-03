package com.atycoin.cli.commands;

import com.atycoin.*;
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

        //-from FROM -to TO -amount AMOUNT
        if (args[0].equals(params[1]) && args[2].equals("-to") && args[4].equals("-amount")) {
            String from = args[1];
            String to = args[3];
            int amount = Integer.parseInt(args[5]);

            if (!Wallet.validateAddress(from)) {
                Commander.CommanderPrint("ERROR: Sender address is not valid");
                return;
            }

            if (!Wallet.validateAddress(to)) {
                Commander.CommanderPrint("ERROR: Recipient address is not valid");
                return;
            }

            if (from.equals(to)) {
                Commander.CommanderPrint("ERROR: You are try to send to your account");
                return;
            }

            if (amount <= 0) {
                Commander.CommanderPrint("ERROR: You are try send negative coin");
                return;
            }

            Transaction newUTXOTransaction = Transaction.newUTXOTransaction(from, to, amount);

            if (newUTXOTransaction == null) {
                Commander.CommanderPrint("ERROR: Invalid Transaction");
                return;
            }

            ArrayList<Transaction> transactions = new ArrayList<>();
            //Reward concept
            //TODO: Make real rewards in Peer2Peer
            transactions.add(Transaction.newCoinbaseTransaction(from));
            transactions.add(newUTXOTransaction);

            Blockchain blockchain = Blockchain.getInstance();
            Block newBlock = blockchain.mineBlock(transactions);
            UTXOSet utxoSet = UTXOSet.getInstance();
            utxoSet.update(newBlock);

            Commander.CommanderPrint("Success!");
        } else if (args[0].equals(params[0])) { //help
            Commander.CommanderPrint(getHelp());
        } else {
            Commander.CommanderPrint("Sorry param not yet implemented");
        }
    }
}
