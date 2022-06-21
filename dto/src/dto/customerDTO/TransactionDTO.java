package dto.customerDTO;

public class TransactionDTO {
    private final int yaz;
    private final char sign;

    private final double amount;
    private final String amountWithSign;
    private final double balanceBefore;
    private final double balanceAfter;
    private final String balanceBeforeString;
    private final String balanceAfterString;


    public TransactionDTO(int yaz, char sign, double amount, double balanceBefore, double balanceAfter) {
        this.yaz = yaz;
        this.sign = sign;
        this.amount = amount;
        this.balanceBefore = balanceBefore;
        this.balanceAfter = balanceAfter;
        amountWithSign=sign + String.format("%.1f", amount);
        balanceBeforeString=String.format("%.1f", balanceBefore);
        balanceAfterString=String.format("%.1f", balanceAfter);

    }



    public int getYaz() {
        return yaz;
    }

    public Character getSign() {
        return sign;
    }

    public double getAmount() {
        return amount;
    }

    public String getAmountWithSign() {
        return amountWithSign;
    }

    public double getBalanceBefore() {
        return balanceBefore;
    }

    public double getBalanceAfter() {
        return balanceAfter;
    }

    public String getBalanceBeforeString() {
        return balanceBeforeString;
    }

    public String getBalanceAfterString() {
        return balanceAfterString;
    }
}
