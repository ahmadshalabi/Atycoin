package com.atycoin.cli.commands;

import com.atycoin.*;
import com.atycoin.cli.Commander;
import com.atycoin.network.Node;
import com.atycoin.network.messages.NetworkMessage;
import com.atycoin.network.messages.TransactionMessage;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class SendCommand implements Command {
    @Override
    public String getHelp() {
        return "cmd: send \n" +
                "- description: Send AMOUNT of coins FROM address to TO. Mine on the same node, when -mine is set.\n" +
                "- usage: send param [situational...] \n" +
                "- param: '-help', '-params', '-from FROM -to TO -amount AMOUNT', " +
                "'-from FROM -to TO -amount AMOUNT -mine' \n" +
                "------------------------------------------------------------------------";
    }

    @Override
    public String[] getParams() {
        return new String[]{"-help", "-params", "-from"};
    }

    @Override
    public void run(String[] args) {
        String[] params = getParams();

        //check if command exists in params list
        if (!Arrays.asList(params).contains(args[0])) {
            Commander.CommanderPrint("ERROR ! unknown parameters...");
            Commander.CommanderPrint(Arrays.toString(params));
        }

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

            Wallet senderWallet = Wallets.getInstance().getWallet(from);
            Transaction newUTXOTransaction = Transaction.newUTXOTransaction(senderWallet, to, amount);

            if (newUTXOTransaction == null) {
                Commander.CommanderPrint("ERROR: Invalid Transaction");
                return;
            }

            if (args.length == 6) {
                try (Socket connection = new Socket(InetAddress.getLocalHost(), Node.getKnownNodes().get(0))) {
                    NetworkMessage message = new TransactionMessage(AtycoinStart.nodeID, newUTXOTransaction);
                    BufferedWriter output = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
                    output.flush();

                    String request = message.makeRequest();
                    output.write(request);
                    output.flush();
                } catch (java.io.IOException e) {
                    throw new RuntimeException(e);
                }
                return;
            }

            if (args[6].equals("-mine")) {
                ArrayList<Transaction> transactions = new ArrayList<>();
                transactions.add(Transaction.newCoinbaseTransaction(from));
                transactions.add(newUTXOTransaction);

                Blockchain blockchain = Blockchain.getInstance();
                Block newBlock = blockchain.mineBlock(transactions);

                if (newBlock == null) {
                    Commander.CommanderPrint("Invalid Block");
                    return;
                }

                UTXOSet utxoSet = UTXOSet.getInstance();
                utxoSet.update(newBlock);
            }

            Commander.CommanderPrint("Success!");
        } else if (args[0].equals(params[0])) { //help
            Commander.CommanderPrint(getHelp());
        } else if (args[0].equals(params[1])) { //-params
            Commander.CommanderPrint(Arrays.toString(params));
        } else {
            Commander.CommanderPrint("Invalid parameter. Enter 'send -help'");
        }
    }
}