package com.atycoin.network.commands;

public class NullCommandHandler extends NetworkCommand {
    @Override
    public void execute(String message, int nodeAddress) {
        //nothing to execute
    }
}