import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class TxHandler {

    private UTXOPool _utxoPool;

    /**
     * Creates a public ledger whose current UTXOPool (collection of unspent transaction outputs) is
     * {@code utxoPool}. This should make a copy of utxoPool by using the UTXOPool(UTXOPool uPool)
     * constructor.
     */
    public TxHandler(UTXOPool utxoPool) {
        _utxoPool = new UTXOPool(utxoPool);
    }

    /**
     * @return true if:
     * (1) all outputs claimed by {@code tx} are in the current UTXO pool, 
     * (2) the signatures on each input of {@code tx} are valid, 
     * (3) no UTXO is claimed multiple times by {@code tx},
     * (4) all of {@code tx}s output values are non-negative, and
     * (5) the sum of {@code tx}s input values is greater than or equal to the sum of its output
     *     values; and false otherwise.
     */
    public boolean isValidTx(Transaction tx) {
        double totalInput = 0;
        double totalOutput = 0;
        int index = 0;
        Set<UTXO> utxoSet = new HashSet<>();
        for(Transaction.Input input : tx.getInputs()) {
            UTXO utxo = new UTXO(input.prevTxHash, input.outputIndex);

            if(utxoSet.contains(utxo)) {
                return false;
            } else {
                utxoSet.add(utxo);
            }

            if(_utxoPool.contains(utxo)) {
                totalInput += _utxoPool.getTxOutput(utxo).value;
            } else {
                return false;
            }

            if(!Crypto.verifySignature(_utxoPool.getTxOutput(utxo).address, tx.getRawDataToSign(index), input.signature)) {
                return false;
            }
            index++;
        }

        index = 0;
        for(Transaction.Output output : tx.getOutputs()) {
            double outputVal = output.value;
            if(outputVal >= 0) {
                totalOutput += outputVal;
            } else {
                return false;
            }
        }

        if(totalOutput > totalInput) {
            return false;
        }

        return true;
    }

    /**
     * Handles each epoch by receiving an unordered array of proposed transactions, checking each
     * transaction for correctness, returning a mutually valid array of accepted transactions, and
     * updating the current UTXO pool as appropriate.
     */
    public Transaction[] handleTxs(Transaction[] possibleTxs) {
        ArrayList<Transaction> validTxs = new ArrayList<>();
        int index = 0;
        for(Transaction transaction : possibleTxs) {
            if(isValidTx(transaction)) {
                for(Transaction.Input input : transaction.getInputs()) {
                    UTXO utxo = new UTXO(input.prevTxHash, input.outputIndex);
                    _utxoPool.removeUTXO(utxo);
                }

                index = 0;
                for(Transaction.Output output : transaction.getOutputs()) {
                    UTXO utxo = new UTXO(transaction.getHash(), index);
                    _utxoPool.addUTXO(utxo, output);
                    index++;
                }

                validTxs.add(transaction);
            }
        }

        return validTxs.toArray(new Transaction[validTxs.size()]);
    }

}
