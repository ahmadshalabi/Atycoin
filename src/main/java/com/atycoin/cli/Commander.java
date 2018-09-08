package com.atycoin.cli;

import com.atycoin.cli.commands.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

//TODO: Add debug command

// responsible for processing command line
public class Commander {
    public static Commander instance = new Commander();
    public static boolean debugMode = false;
    public HashMap<String, Command> commands;
    public Scanner scanner;

    private Commander() {
        setup();
    }

    public static Commander getInstance() {
        return instance;
    }

    public static void CommanderPrint(String msg) {
        System.out.println("- " + msg);
    }

    public static void CommanderInput(String msg) {
        System.out.println(msg + ": ");
    }

    // populates commands in a map
    public void setup() {
        commands = new HashMap<>();
        commands.put("createblockchain", new CreateBlockchainCommand());
        commands.put("createwallet", new CreateWalletCommand());
        commands.put("getbalance", new GetBalanceCommand());
        commands.put("printchain", new PrintChainCommand());
        commands.put("listaddresses", new ListAddressesCommand());
        commands.put("send", new SendCommand());
        commands.put("reindexutxo", new ReIndexUTXOCommand());
        commands.put("startnode", new StartNodeCommand());
        commands.put("help", new HelpCommand());
        scanner = new Scanner(System.in);
    }

    // parses command line arguments and processes commands
    public void listen() {
        while (true) {
            //Display input message:
            CommanderInput("Atycoin-cli");

            //Gather user input
            String rawInput = scanner.nextLine();

            //check if the users wishes to quit
            if (rawInput.equals("quit") || rawInput.equals("exit")) {
                break;
            }

            //Gather the raw arguments entered by the user
            String[] rawArgs = rawInput.split("\\s+");

            //check any command or argument was entered
            if (!rawArgs[0].equals("")) {
                //check if debug mode is false, if so then run command in try catch
                // to avoid crashing the application if an error is thrown
                if (!debugMode) {
                    try {
                        call(rawArgs);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        CommanderPrint("command couldn't execute, perhaps not enough arguments? try: " + rawArgs[0] + " -help");
                    } catch (Exception e) {
                        CommanderPrint("command failed to execute.");
                        e.printStackTrace();
                    }
                } else { //Otherwise run the command with no safety net.
                    call(rawArgs);
                }
            }
        }
    }

    //get command object from commands and call command.run(args)
    public void call(String[] rawArgs) {
        String function = rawArgs[0];
        String[] args = Arrays.copyOfRange(rawArgs, 1, rawArgs.length);

        Command command = commands.get(function);
        if (command == null) {
            CommanderPrint("command function: '" + function + "' not found. Type help for a list of functions");
        } else {
            command.run(args);
        }
    }
}