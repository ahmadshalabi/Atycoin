package com.atycoin;

import org.bouncycastle.jce.interfaces.ECPrivateKey;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

//Blockchain implements interaction with a DB
public class Blockchain implements Iterable<Block> {
    private static Blockchain instance;
    private static Jedis dbConnection;
    private String tipOfChain;

    //TODO: Do manual commit and support rollback (BulkTransaction in DB)
    private Blockchain(int port) {
        dbConnection = new Jedis("localhost", port);
    }

    public static Blockchain getInstance() {
        int nodeID = AtycoinStart.getNodeID();
        if (instance == null) {
            instance = new Blockchain(nodeID + 3379);
        }
        instance.tipOfChain = dbConnection.get("l");
        return instance;
    }

    //TODO: Check failed connection
    // creates a new Blockchain with genesis Block
    public void createBlockchain(String address) {
        if (tipOfChain == null) {
            System.out.println("No existing blockchain found. Creating a new one...\n");

            Transaction coinbaseTransaction = Transaction.newCoinbaseTransaction(address);
            Block genesisBlock = Block.newGenesisBlock(coinbaseTransaction);

            UTXOSet utxoSet = UTXOSet.getInstance();
            utxoSet.update(genesisBlock);

            tipOfChain = Util.serializeHash(genesisBlock.getHash());

            dbConnection.set(tipOfChain, genesisBlock.serializeBlock());
            dbConnection.set("l", tipOfChain);

            System.out.println("Done!");
        } else {
            System.out.println("Blockchain already exists.");
        }
    }

    //saves the block into the blockchain
    public boolean addBlock(Block block) {
        String blockHashSerialized = Util.serializeHash(block.getHash());
        String blockInDb = dbConnection.get(blockHashSerialized);

        // block already exist
        if (blockInDb != null) {
            return false;
        }

        String blockSerialized = block.serializeBlock();

        String lastHashSerialized = dbConnection.get("l");

        if (lastHashSerialized == null) {
            dbConnection.set(blockHashSerialized, blockSerialized);
            dbConnection.set("l", blockHashSerialized);
            tipOfChain = blockHashSerialized;
            return true;
        }

        String lastBlockSerialized = dbConnection.get(lastHashSerialized);

        Block lastBlock = Block.deserializeBlock(lastBlockSerialized);

        if (block.getHeight() > lastBlock.getHeight()) {
            dbConnection.set(blockHashSerialized, blockSerialized);
            dbConnection.set("l", blockHashSerialized);
            tipOfChain = blockHashSerialized;
            return true;
        }

        return false;
    }

    //TODO: Check if you can replace it using UTXOSet
    // finds a transaction by its ID
    public Transaction findTransaction(byte[] id) {
        for (Block block : this) {
            for (Transaction tx : block.transactions) {
                if (Arrays.equals(id, tx.id)) {
                    return tx;
                }
            }
        }
        return null; // Transaction is not found
    }

    // finds all unspent transaction outputs and returns transactions with unspent outputs
    public HashMap<String, ArrayList<TransactionOutput>> findUTXO() {
        HashMap<String, ArrayList<TransactionOutput>> UTXO = new HashMap<>();
        HashMap<String, ArrayList<Integer>> spentTXOs = new HashMap<>();

        //TODO: Optimize logic in find unspentTransactions
        for (Block block : this) {
            for (Transaction transaction : block.transactions) {
                String transactionId = Util.serializeHash(transaction.id);

                for (TransactionOutput transactionOutput : transaction.outputs) {
                    boolean isTransactionOutputSpent = false;
                    int outIdx = transaction.outputs.indexOf(transactionOutput);

                    // Was the output spent?
                    ArrayList<Integer> spentTransactionOutputIndexes = spentTXOs.get(transactionId);
                    if (spentTransactionOutputIndexes != null) {
                        for (int spentOutIndex : spentTransactionOutputIndexes) {
                            if (spentOutIndex == outIdx) {
                                isTransactionOutputSpent = true;
                                break;
                            }
                        }
                    }

                    if (isTransactionOutputSpent) {
                        continue;
                    }

                    ArrayList<TransactionOutput> outs = UTXO.computeIfAbsent(transactionId, k -> new ArrayList<>());
                    outs.add(transactionOutput);
                }

                for (TransactionInput transactionInput : transaction.inputs) {
                    String prevTransactionId = Util.serializeHash(transactionInput.prevTransactionId);

                    ArrayList<Integer> spentTransactionOutputIndexes =
                            spentTXOs.computeIfAbsent(prevTransactionId, k -> new ArrayList<>());
                    spentTransactionOutputIndexes.add(transactionInput.transactionOutputIndex);
                }
            }
        }
        return UTXO;
    }

    @Override
    //return BlockchainIterator to iterate over blockchain in DB
    public Iterator<Block> iterator() {
        return new BlockchainIterator(dbConnection);
    }

    // returns the height of the latest block
    public int getBestHeight() {
        String lastHash = dbConnection.get("l");

        if (lastHash == null) {
            return 0;
        }

        String blockData = dbConnection.get(lastHash);

        Block lastBlock = Block.deserializeBlock(blockData);

        return lastBlock.getHeight();
    }

    // finds a block by its hash and returns it
    public Block getBlock(String blockHash) {
        String blockSerialized = dbConnection.get(blockHash);

        if (blockSerialized == null) {
            System.out.println("Block is not found");
        }
        return Block.deserializeBlock(blockSerialized);
    }

    // returns a list of required block hashes from the chain
    public ArrayList<String> getBlockHashes(int requiredHeight) {
        ArrayList<String> blockHashes = new ArrayList<>();
        for (Block block : this) {
            if (requiredHeight != 0 && block.getHeight() <= requiredHeight) {
                break;
            }

            blockHashes.add(Util.serializeHash(block.getHash()));
        }

        return blockHashes;
    }

    //TODO: Check failed connection
    // mines a new block with the provided transactions
    public Block mineBlock(ArrayList<Transaction> transactions) {
        for (int i = 0, size = transactions.size(); i < size; i++) {
            if (!verifyTransaction(transactions.get(0))) {
                System.out.println("ERROR: Invalid transactions");
                transactions.remove(transactions.get(0));
            }
        }

        String lastBlock = dbConnection.get(tipOfChain);
        Block block = Block.deserializeBlock(lastBlock);
        int lastHeight = block.getHeight();

        Block newBlock = Block.newBlock(transactions, Util.deserializeHash(tipOfChain), lastHeight + 1);

        tipOfChain = Util.serializeHash(newBlock.getHash());
        String newBlockSerialized = newBlock.serializeBlock();

        //Store new block in database and update hash of last block
        dbConnection.set(tipOfChain, newBlockSerialized);
        dbConnection.set("l", tipOfChain);

        return newBlock;
    }

    // signs inputs of a Transaction
    public boolean signTransaction(Transaction tx, ECPrivateKey privateKey) {
        HashMap<String, Transaction> prevTXs = new HashMap<>();

        for (TransactionInput input : tx.inputs) {
            Transaction prevTX = findTransaction(input.prevTransactionId);
            if (prevTX == null) {
                System.out.println("Transaction is not Found");
                return false;
            }
            prevTXs.put(Util.serializeHash(prevTX.id), prevTX);
        }

        tx.sign(privateKey, prevTXs);
        return true;
    }

    // verifies transaction input signatures
    public boolean verifyTransaction(Transaction tx) {
        if (tx.isCoinbaseTransaction()) {
            return true;
        }

        HashMap<String, Transaction> prevTXs = new HashMap<>();

        for (TransactionInput input : tx.inputs) {
            Transaction prevTX = findTransaction(input.prevTransactionId);
            if (prevTX == null) {
                System.out.println("Transaction is not Found");
                return false;
            }
            prevTXs.put(Util.serializeHash(prevTX.id), prevTX);
        }

        return tx.verify(prevTXs);
    }
}