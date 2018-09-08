package com.atycoin.cli.commands;

import com.atycoin.cli.Commander;

import java.util.Arrays;

public class DebugModeCommand implements Command {
    @Override
    public String getHelp() {
        return "cmd: debug \n" +
                "- description: Toggle debug mode on the cli. " +
                "Causing the application to potentially crash if an error is thrown. \n" +
                "- usage: debug true/false \n" +
                "------------------------------------------------------------------------";
    }

    @Override
    public String[] getParams() {
        return new String[]{"-help", "-params", "true", "false"};
    }

    @Override
    public void run(String[] args) {
        if (args.length < 1) {
            Commander.CommanderPrint("Debug mode active: " + Commander.getInstance().getDebugMode());
            return;
        }

        if (!Arrays.asList(getParams()).contains(args[0])) {
            Commander.CommanderPrint("ERROR! unknown parameters...");
            Commander.CommanderPrint(Arrays.toString(getParams()));
            return;
        }

        String[] params = getParams();

        if (args[0].equals(params[2])) { //true
            Commander.getInstance().setDebugMode(true);
            Commander.CommanderPrint("Setting Debug mode to true...");
            Commander.CommanderPrint("Warning application may now crash if an error is thrown!");
            Commander.CommanderPrint("Debug mode active: " + Commander.getInstance().getDebugMode());
        } else if (args[0].equals(params[3])) { //false
            Commander.getInstance().setDebugMode(false);
            Commander.CommanderPrint("Setting Debug mode to false...");
            Commander.CommanderPrint("Application will not crash if an error is thrown.");
            Commander.CommanderPrint("Debug mode active: " + Commander.getInstance().getDebugMode());
        } else if (args[0].equals(params[0])) { //help
            Commander.CommanderPrint(getHelp());
        } else if (args[0].equals(params[1])) { //params
            Commander.CommanderPrint(Arrays.toString(getParams()));
        } else {
            Commander.CommanderPrint("Sorry param not implemented. debug-cli -help");
        }
    }
}
