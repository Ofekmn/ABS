package engine.loan;

import java.io.Serializable;

public class Payment implements Serializable {
    private final int yaz;
    private final double amount;
    private final double interest;

    public Payment(int yaz, double amount, double interest) {
        this.yaz = yaz;
        this.amount = amount;
        this.interest = interest;
    }

    public int getYaz() {
        return yaz;
    }

    public double getAmount() {
        return amount;
    }

    public double getInterest() {
        return interest;
    }

}
