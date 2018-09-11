package com.atycoin.database;

import com.atycoin.AtycoinStart;
import com.atycoin.TransactionOutput;
import com.atycoin.utility.Hash;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import redis.clients.jedis.Jedis;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

public class ChainStateDAO {
    private static final int CHAIN_STATE_DB = 1;
    private static ChainStateDAO instance;
    private static Jedis dbConnection;

    private ChainStateDAO(int port) {
        dbConnection = new Jedis("localhost", port);
        dbConnection.select(CHAIN_STATE_DB);
    }

    public static ChainStateDAO getInstance() {
        if (instance == null) {
            int port = AtycoinStart.getNodeID() + 3379; //shift to simulate multiple node
            instance = new ChainStateDAO(port);
        }
        return instance;
    }

    public void setUnspentOutputs(byte[] id, List<TransactionOutput> outputs) {
        String key = getKey(id);
        String value = serializeOutputs(outputs);
        dbConnection.set(key, value);
    }

    public void deleteUnspentOutputs(byte[] id) {
        String key = getKey(id);
        dbConnection.del(key);
    }

    public Set<String> getAllReferenceTransaction() {
        return dbConnection.keys("*");
    }

    public List<TransactionOutput> getReferenceOutputs(byte[] id) {
        String key = getKey(id);
        return getReferenceOutputs(key);
    }

    public List<TransactionOutput> getReferenceOutputs(String key) {
        String outputsSerialized = dbConnection.get(key);
        return deserializeOutputs(outputsSerialized);
    }

    private String getKey(byte[] id) {
        return Hash.serialize(id);
    }

    private String serializeOutputs(List<TransactionOutput> outputs) {
        return new Gson().toJson(outputs);
    }

    private List<TransactionOutput> deserializeOutputs(String outputSerialized) {
        Type listTypeToken = new TypeToken<List<TransactionOutput>>() {
        }.getType();

        return new Gson().fromJson(outputSerialized, listTypeToken);
    }
}