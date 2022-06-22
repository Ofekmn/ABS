package engine.exception.xml;

public class YazException extends Exception {
    private final String loanID;
    private final int totalYaz;
    private final int paymentRate;

    public YazException(String loanID, int totalYaz, int paymentRate) {
        this.loanID = loanID;
        this.totalYaz = totalYaz;
        this.paymentRate = paymentRate;
    }

    public String getLoanID() {
        return loanID;
    }

    public int getTotalYaz() {
        return totalYaz;
    }

    public int getPaymentRate() {
        return paymentRate;
    }

    public String getMessage() {return "For the loan: " + getLoanID() + ", the total loan time (" + getTotalYaz() +
                  ") isn't divided by the payment rate (" + getPaymentRate() + ")";}
}
