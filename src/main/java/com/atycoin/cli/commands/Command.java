package com.atycoin.cli.commands;

public interface Command {
    String getHelp();

    String[] getParams();

    void run(String[] args);
}
