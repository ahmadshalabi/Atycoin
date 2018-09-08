package com.atycoin.cli.commands;

import com.atycoin.cli.Commander;

import java.util.Collection;

public class HelpCommand implements Command {
    @Override
    public String getHelp() {
        return "cmd: help \n" +
                "- description: Displays help for all known command. \n" +
                "------------------------------------------------------------------------";
    }

    @Override
    public String[] getParams() {
        throw new UnsupportedOperationException("Not Supported");
    }

    @Override
    public void run(String[] args) {
        Commander.CommanderPrint("\n------------------------------------------------------------------------\n" +
                "-  ATYCOIN HELP\n" +
                "------------------------------------------------------------------------");

        Commander commander = Commander.getInstance();
        Collection<Command> commands = commander.getCommands();
        for (Command command : commands) {
            Commander.CommanderPrint(command.getHelp());
        }
    }
}