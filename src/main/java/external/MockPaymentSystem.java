package external;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

/**
 * A mock implementation of PaymentSystem for testing purposes.
 * In a real life application, this implementation would be making network requests to a payment service API.
 * However, networking is the topic of another course (if this sounds interesting, you may want to take
 * COMN - Computer Communications and Networks in year 3 or 4).
 * 
 * This class should keep track of payments made, so that when a refund is requested, it can check whether the
 * transactionAmount corresponds to a previously made payment (or if something fishy may be going on).
 * Watch out for transactions made between the same people for the same amount more than once!
 * 
 * Hint: you may find it helpful to use an inner Transaction class, overriding its equals and hashCode methods
 */


public class MockPaymentSystem extends Object implements PaymentSystem {
    private List<Transaction> transactions = new ArrayList<Transaction>();

    public MockPaymentSystem() {}
    /**
     * @param buyerAccountEmail email address of the buyer's account on the payment system
     * @param sellerAccountEmail email address of the seller's account on the payment system
     * @param transactionAmount amount to be transferred in GBP
     * @return True if successful and false otherwise
     * {@inheritDoc}
     */
    public boolean processPayment(String buyerAccountEmail, String sellerAccountEmail, double transactionAmount) {
        transactions.add(new Transaction(buyerAccountEmail,sellerAccountEmail,transactionAmount,TransactionType.PAYMENT));
        return true;
    }

    /**
     * @param buyerAccountEmail email address of the buyer's account on the payment system
     * @param sellerAccountEmail email address of the seller's account on the payment system
     * @param transactionAmount amount to be transferred in GBP
     * @return True if successful and false otherwise
     * {@inheritDoc}
     */
    public boolean processRefund(String buyerAccountEmail, String sellerAccountEmail, double transactionAmount) {
        Transaction transaction = new Transaction(buyerAccountEmail,sellerAccountEmail,transactionAmount,TransactionType.REFUND);
        ArrayList<Transaction> unmatchedTransactions = getUnmatchedTransactions(transaction);
        // Checks only one transaction that can be refunded
        if (unmatchedTransactions.size() == 1){
            transactions.add(transaction);
            unmatchedTransactions.get(0).setMatched();
            return true;
        }
        return false;
    }

    /**
     *
     * @param transaction Transaction to check if it has been refunded
     * @return unmatchedTransaction a list of transaction payments which is equal to the parameter transaction and
     *                              has not been refunded
     */
    private ArrayList<Transaction> getUnmatchedTransactions(Transaction transaction){
        ArrayList<Transaction> unMatchedTransactions = new ArrayList<Transaction>();
        for (Transaction t : this.transactions){
            if (t.getBuyerAccountEmail().equals(transaction.getBuyerAccountEmail()) &&
                    t.getSellerAccountEmail().equals(transaction.getSellerAccountEmail())
                    && t.getTransactionAmount() == transaction.getTransactionAmount()){
                if (!t.getMatched() & t.getTransactionType() == TransactionType.PAYMENT){
                    unMatchedTransactions.add(t);
                }
            }
        }
        return unMatchedTransactions;
    }

    static class Transaction{
        private String buyerAccountEmail;
        private String sellerAccountEmail;
        private double transactionAmount;
        private TransactionType type;
        private boolean matched;

        protected Transaction(String buyerAccountEmail, String sellerAccountEmail, double transactionAmount, TransactionType type){
            this.buyerAccountEmail = buyerAccountEmail;
            this.sellerAccountEmail = sellerAccountEmail;
            this.transactionAmount = transactionAmount;
            this.type = type;
            this.matched = false;
        }

        public String getBuyerAccountEmail() {
            return buyerAccountEmail;
        }

        public String getSellerAccountEmail() {
            return sellerAccountEmail;
        }

        public double getTransactionAmount() {
            return transactionAmount;
        }

        public TransactionType getTransactionType(){
            return this.type;
        }

        public boolean getMatched(){
            return this.matched;
        }

        public void setMatched(){
            this.matched = true;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Transaction that = (Transaction) o;
            return Double.compare(that.transactionAmount, transactionAmount) == 0 && buyerAccountEmail.equals(that.buyerAccountEmail) && sellerAccountEmail.equals(that.sellerAccountEmail);
        }

        @Override
        public int hashCode() {
            return Objects.hash(buyerAccountEmail, sellerAccountEmail, transactionAmount, type);
        }
    }
    enum TransactionType{
        PAYMENT,REFUND
    }

}
