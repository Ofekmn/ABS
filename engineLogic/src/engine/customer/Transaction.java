package engine.customer;

import java.io.Serializable;

public class Transaction implements Serializable {
    private final int yaz;
    private final double price;
    private final char sign;
    private final double amountBeforeTransaction;
    private final double amountAfterTransaction;

    public Transaction(int yaz, double price, char sign, double amountBeforeTransaction, double amountAfterTransaction) {
        this.yaz = yaz;
        this.price = price;
        this.sign = sign;
        this.amountBeforeTransaction = amountBeforeTransaction;
        this.amountAfterTransaction = amountAfterTransaction;
    }

    public int getYaz() {
        return yaz;
    }

    public double getPrice() {
        return price;
    }

    public char getSign() {
        return sign;
    }

    public double getAmountBeforeTransaction() {
        return amountBeforeTransaction;
    }

    public double getAmountAfterTransaction() {
        return amountAfterTransaction;
    }



}
