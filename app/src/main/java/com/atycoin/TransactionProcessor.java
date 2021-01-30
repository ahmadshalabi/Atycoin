package com.atycoin;

import com.atycoin.database.BlocksDAO;
import com.atycoin.utility.Constant;
import com.atycoin.utility.Hash;
import com.atycoin.utility.Signature;
import org.bouncycastle.jce.interfaces.ECPrivateKey;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransactionProcessor {
    private Transaction transaction;
    private BlocksDAO blocksDAO;

    public TransactionProcessor(Transaction transaction) {
        this.transaction = transaction;
        blocksDAO = BlocksDAO.getInstance();
    }

    public boolean signTransaction(ECPrivateKey privateKey) {
        if (transaction.isCoinbaseTransaction()) {
            return true;
        }

        Map<String, Transaction> referenceTransactions = getReferenceTransactions();
        if (referenceTransactions == null) {
            return false;
        }

        Transaction trimmedCopy = Transaction.trimmedTransaction(transaction);
        List<TransactionInput> trimmedCopyInputs = trimmedCopy.getInputs();

        for (TransactionInput input : trimmedCopyInputs) {
            TransactionOutput referenceOutput = getReferenceOutput(referenceTransactions, input);
            setRawPublicKey(input, referenceOutput);

            //sign id data
            trimmedCopy.setID();
            byte[] signature = Signature.sign(privateKey, trimmedCopy.getId());

            int index = getCorrespondingIndex(trimmedCopyInputs, input);
            TransactionInput transactionInput = getCorrespondingInput(transaction.getInputs(), index);
            transactionInput.setSignature(signature);

            // Remove publicKey from input
            input.setRawPublicKey(Constant.EMPTY_BYTE_ARRAY);
        }
        return true;
    }

    // verifies transaction input signatures
    public boolean verifyTransaction() {
        if (transaction.isCoinbaseTransaction()) {
            return true;
        }

        Map<String, Transaction> referenceTransactions = getReferenceTransactions();
        if (referenceTransactions == null) {
            return false;
        }

        Transaction trimmedCopy = Transaction.trimmedTransaction(transaction);

        boolean validTransaction = true;
        List<TransactionInput> inputs = transaction.getInputs();
        for (TransactionInput input : inputs) {
            TransactionOutput referenceOutput = getReferenceOutput(referenceTransactions, input);

            int index = getCorrespondingIndex(inputs, input);
            TransactionInput trimmedCopyInput = getCorrespondingInput(trimmedCopy.getInputs(), index);
            setRawPublicKey(trimmedCopyInput, referenceOutput);

            //Verify Data
            byte[] rawPublicKey = input.getRawPublicKey();
            trimmedCopy.setID();
            byte[] signature = input.getSignature();
            validTransaction = Signature.verify(rawPublicKey, trimmedCopy.getId(), signature);

            if (!validTransaction) {
                break;
            }

            // Remove publicKey from trimmedCopyInput
            trimmedCopyInput.setRawPublicKey(Constant.EMPTY_BYTE_ARRAY);
        }
        return validTransaction;

    }

    private Map<String, Transaction> getReferenceTransactions() {
        Map<String, Transaction> referenceTransactions = new HashMap<>();
        for (TransactionInput input : transaction.getInputs()) {
            byte[] referenceTransactionID = input.getReferenceTransaction();
            Transaction referenceTransaction = findTransaction(referenceTransactionID);
            if (referenceTransaction == null) {
                System.out.println("Transaction is not Found");
                return null;
            }
            String key = Hash.serialize(referenceTransactionID);
            referenceTransactions.put(key, referenceTransaction);
        }
        return referenceTransactions;
    }

    private Transaction findTransaction(byte[] id) {
        for (Block block : blocksDAO) {
            for (Transaction transaction : block.getTransactions()) {
                if (Arrays.equals(id, transaction.getId())) {
                    return transaction;
                }
            }
        }
        return null;
    }

    private TransactionOutput getReferenceOutput(Map<String, Transaction> referenceTransactions, TransactionInput input) {
        //get reference transaction
        byte[] id = input.getReferenceTransaction();
        String referenceID = Hash.serialize(id);
        Transaction referenceTransaction = referenceTransactions.get(referenceID);

        //get reference outputs
        List<TransactionOutput> referenceOutputs = referenceTransaction.getOutputs();

        //get reference output
        int referenceIndex = input.getOutputIndex();
        return referenceOutputs.get(referenceIndex);
    }

    private int getCorrespondingIndex(List<TransactionInput> inputs, TransactionInput input) {
        return inputs.indexOf(input);
    }

    private TransactionInput getCorrespondingInput(List<TransactionInput> inputs, int index) {
        return inputs.get(index);
    }

    private void setRawPublicKey(TransactionInput input, TransactionOutput referenceOutput) {
        byte[] publicKeyHashed = referenceOutput.getPublicKeyHashed();
        input.setRawPublicKey(publicKeyHashed);
    }
}