package com.atycoin.network.commands;

import java.util.HashMap;
import java.util.Map;

public class NetworkCommandFactory {
    private static final NetworkCommand NULL_COMMAND = new NullCommandHandler();
    private static final Map<String, NetworkCommand> commands;

    static {
        commands = new HashMap<>();
        initializeCommands();
    }

    private static void initializeCommands() {
        commands.put("addresses", new AddressesCommandHandler());
        commands.put("block", new BlockCommandHandler());
        commands.put("getblocks", new GetBlocksCommandHandler());
        commands.put("getdata", new GetDataCommandHandler());
        commands.put("inventory", new InventoryCommandHandler());
        commands.put("transaction", new TransactionCommandHandler());
        commands.put("version", new VersionCommandHandler());
        commands.put("null", new NullCommandHandler());
    }

    public static NetworkCommand getCommand(String command) {
        NetworkCommand networkCommand = commands.get(command);

        if (networkCommand == null) {
            networkCommand = NULL_COMMAND;
        }
        return networkCommand;
    }
}