import java.util.ArrayList;

/**
 * Created by XV880UE on 06.12.2016.
 */
public class MaxFeeTxHandler {
    /**
     * Handles each epoch by receiving an unordered array of proposed transactions, checking each
     * transaction for correctness, returning a mutually valid array of accepted transactions, and
     * updating the current UTXO pool as appropriate.
     */
    public Transaction[] handleTxs(Transaction[] possibleTxs) {
        ArrayList<Transaction> validTxs = new ArrayList<>();
        int index = 0;
        for(Transaction transaction : possibleTxs) {
           
        }

        return validTxs.toArray(new Transaction[validTxs.size()]);
    }

}
