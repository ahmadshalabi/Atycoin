package com.atycoin.cli.commands;

import com.atycoin.cli.Commander;

public class HelpCommand implements Command {
    @Override
    public String getHelp() {
        return "cmd: -help \n" +
                "- description: Displays help for all known command. \n" +
                "------------------------------------------------------------------------";
    }

    @Override
    public String[] getParams() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void run(String[] args) {
        Commander.CommanderPrint("\n------------------------------------------------------------------------\n" +
                "-  ATYCOIN HELP\n" +
                "------------------------------------------------------------------------");

        for (String key : Commander.getInstance().cmds.keySet()) {
            Commander.CommanderPrint(Commander.getInstance().cmds.get(key).getHelp());
        }
    }
}
