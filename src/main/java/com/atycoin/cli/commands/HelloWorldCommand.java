package com.atycoin.cli.commands;

import com.atycoin.cli.Commander;

import java.util.Arrays;

public class HelloWorldCommand implements Command {
    @Override
    public String getHelp() {
        return "cmd: hello-world \n" +
                "- description: Displays hello-world. \n" +
                "- usage: hello-world param [situational...] \n" +
                "- param: 'hello', '-help' \n" +
                "------------------------------------------------------------------------";
    }

    @Override
    public String[] getParams() {
        return new String[]{"-help", "hello"};
    }

    @Override
    public void run(String[] args) {
        //default behaviour
        if (args.length == 0) {
            Commander.CommanderPrint("Hello World!");
            return;
        }

        //check if command exists in params list
        if (!Arrays.asList(getParams()).contains(args[0])) {
            Commander.CommanderPrint("ERROR ! unknown parameters...");
            Commander.CommanderPrint(Arrays.toString((getParams())));
            return;
        }

        //check if the user entered the hello parameter e.g hello-world hello
        if (args[0].equals("hello")) {
            Commander.CommanderPrint("...World");
            return;
        } else if (args[0].equals("-help")) {
            Commander.CommanderPrint(getHelp());
        }
    }
}
