package com.atycoin.cli;

import com.atycoin.cli.commands.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

//TODO: Add debug command

public class Commander {
    public static Commander instance;
    public static boolean debugMode = false;
    public HashMap<String, Command> cmds;
    public Scanner scanner;

    public Commander() {
        setup();
        instance = this;
    }

    public static Commander getInstance() {
        if (instance != null) {
            instance = new Commander();
        }

        return instance;
    }

    public static void CommanderPrint(String msg) {
        System.out.println("- " + msg);
    }

    public static String CommanderInput(String msg) {
        System.out.println(msg + ": ");
        return null;
    }

    //get command object from cmds and call command.run(args)
    public void call(String[] rawArgs) {
        String function = rawArgs[0];
        String[] args = Arrays.copyOfRange(rawArgs, 1, rawArgs.length);

        Command command = cmds.get(function);
        if (command == null) {
            CommanderPrint("command function: '" + function + "' not found. Type -help for a list of functions");
        } else {
            command.run(args);
        }
    }

    // populates the command arraylist with commands
    public void setup() {
        cmds = new HashMap<>();
        cmds.put("addblock", new AddBlockCommand());
        cmds.put("printchain", new PrintChainCommand());
        cmds.put("createblockchain", new CreateBlockchainCommand());
        cmds.put("help", new HelpCommand());
        scanner = new Scanner(System.in);
    }

    public void listen() {
        while (true) {
            //Display input message:
            CommanderInput("atycoin-cli");

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
}
