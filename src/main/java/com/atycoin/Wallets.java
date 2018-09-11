package com.atycoin;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Wallets {
    private static final String WALLETS_FILE = String.format("wallets%d.txt", AtycoinStart.getNodeID());
    private static final Wallets instance = new Wallets();
    private final String directory;
    private Map<String, Wallet> wallets;

    private Wallets() {
        directory = getClass().getResource("").getPath();
        loadFromFile();
    }

    public static Wallets getInstance() {
        return instance;
    }

    // adds a Wallet to Wallets
    public String createWallet() {
        Wallet wallet = Wallet.newWallet();
        String address = wallet.getAddress();
        wallets.put(address, wallet);
        saveToFile();
        return address;
    }

    // returns a list of addresses stored in the wallet file
    public List<String> getAddresses() {
        return new ArrayList<>(wallets.keySet());
    }

    // returns a Wallet by its address
    public Wallet getWallet(String address) {
        return wallets.get(address);
    }

    // loads wallets from the file
    private void loadFromFile() {
        if (!Files.exists(Paths.get(directory, WALLETS_FILE))) {
            wallets = new HashMap<>();
            return;
        }

        try (BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(directory, WALLETS_FILE))) {
            String deserializeWallets = bufferedReader.readLine();

            if (deserializeWallets == null) {
                wallets = new HashMap<>();
            } else {
                Gson decoder = new Gson();
                Type mapTypeToken = new TypeToken<Map<String, Wallet>>() {
                }.getType();

                wallets = decoder.fromJson(deserializeWallets, mapTypeToken);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // saves wallets to a file
    private void saveToFile() {
        Gson encoder = new Gson();
        String serializedWallets = encoder.toJson(wallets);

        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(Paths.get(directory, WALLETS_FILE))) {
            bufferedWriter.write(serializedWallets);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}