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

public class WalletsProcessor {
    private static final String WALLETS_FILE = "wallets.txt";
    public transient final String directory;
    public HashMap<String, Wallet> wallets;

    private WalletsProcessor() {
        directory = getClass().getResource("").getPath();
    }

    // creates Wallets and fills it from a file if it exists
    public static WalletsProcessor newWalletsProcessor() {
        WalletsProcessor newWalletsProcessor = new WalletsProcessor();

        newWalletsProcessor.loadFromFile();

        return newWalletsProcessor;
    }

    // adds a Wallet to Wallets
    public String createWallet() {
        Wallet wallet = Wallet.newWallet();
        String address = wallet.getAddress();
        wallets.put(address, wallet);
        return address;
    }

    // returns a list of addresses stored in the wallet file
    public ArrayList<String> getAddresses() {
        return new ArrayList<>(wallets.keySet());
    }

    // returns a Wallet by its address
    public Wallet getWallet(String address) {
        return wallets.get(address); // Check null
    }

    // loads wallets from the file
    public void loadFromFile() {
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
                Type hashMapTypeToken = new TypeToken<HashMap<String, Wallet>>() {
                }.getType();

                wallets = decoder.fromJson(deserializeWallets, hashMapTypeToken);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // saves wallets to a file
    public void saveToFile() {
        Gson encoder = new Gson();
        String serializedWallets = encoder.toJson(wallets);

        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(Paths.get(directory, WALLETS_FILE))) {
            bufferedWriter.write(serializedWallets);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}