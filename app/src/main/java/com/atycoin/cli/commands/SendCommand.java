package com.atycoin.cli.commands;

import com.atycoin.*;
import com.atycoin.cli.Commander;
import com.atycoin.network.KnownNodes;
import com.atycoin.network.messages.NetworkMessage;
import com.atycoin.network.messages.TransactionMessage;
import com.atycoin.utility.Address;
import org.bouncycastle.jce.interfaces.ECPrivateKey;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
            return;
        }

        //-from FROM -to TO -amount AMOUNT
        if (args[0].equals(params[2]) && args[2].equals("-to") && args[4].equals("-amount")) {
            String from = args[1];
            String to = args[3];
            int amount = Integer.parseInt(args[5]);

            if (!Address.isValidAddress(from)) {
                Commander.CommanderPrint("ERROR: Sender address is not valid");
                return;
            }

            if (!Address.isValidAddress(to)) {
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
            Transaction newTransaction = Transaction.newTransaction(senderWallet, to, amount);

            ECPrivateKey privateKey = senderWallet.getPrivateKey();
            TransactionProcessor transactionProcessor = new TransactionProcessor(newTransaction);
            boolean isSignedCorrectly = transactionProcessor.signTransaction(privateKey);

            if (!isSignedCorrectly) {
                Commander.CommanderPrint("ERROR: Invalid Transaction");
                return;
            }

            if (args.length == 6) {
                try (Socket connection = new Socket(InetAddress.getLocalHost(), KnownNodes.get(0))) {
                    NetworkMessage message = new TransactionMessage(AtycoinStart.getNodeID(), newTransaction);
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
                List<Transaction> transactions = new ArrayList<>();
                transactions.add(Transaction.newCoinbaseTransaction(from));
                transactions.add(newTransaction);

                Block newBlock = Blockchain.getInstance().mineBlock(transactions);

                if (newBlock == null) {
                    Commander.CommanderPrint("Invalid Block");
                    return;
                }
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