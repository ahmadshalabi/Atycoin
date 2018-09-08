package com.atycoin.network.commands;

public interface NetworkCommand {
    void execute(String message, int nodeAddress);
}