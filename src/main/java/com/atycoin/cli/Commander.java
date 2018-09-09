package com.atycoin.cli;

import com.atycoin.cli.commands.*;

import java.util.*;

// responsible for processing command line
public class Commander {
    private static Commander instance = new Commander();
    private boolean debugMode = false;
    private Map<String, Command> commands;
    private Scanner scanner;

    private Commander() {
        setup();
    }

    public static Commander getInstance() {
        return instance;
    }

    public static void CommanderPrint(String message) {
        System.out.println("- " + message);
    }

    private static void CommanderInput(String message) {
        System.out.println(message + ": ");
    }

    // populates commands in a map
    private void setup() {
        commands = new HashMap<>();
        commands.put("createblockchain", new CreateBlockchainCommand());
        commands.put("createwallet", new CreateWalletCommand());
        commands.put("debug", new DebugModeCommand());
        commands.put("getbalance", new GetBalanceCommand());
        commands.put("help", new HelpCommand());
        commands.put("listaddresses", new ListAddressesCommand());
        commands.put("printchain", new PrintChainCommand());
        commands.put("send", new SendCommand());
        commands.put("startnode", new StartNodeCommand());
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
                    }
                } else { //Otherwise run the command with no safety net.
                    call(rawArgs);
                }
            }
        }
    }

    //get command object from commands and call command.run(args)
    private void call(String[] rawArgs) {
        String function = rawArgs[0];
        String[] args = Arrays.copyOfRange(rawArgs, 1, rawArgs.length);

        Command command = commands.get(function);
        if (command == null) {
            CommanderPrint("command function: '" + function + "' not found. Type help for a list of functions");
        } else {
            command.run(args);
        }
    }

    public Collection<Command> getCommands() {
        return commands.values();
    }

    public boolean getDebugMode() {
        return debugMode;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }
}