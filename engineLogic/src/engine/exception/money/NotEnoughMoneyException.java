package engine.exception.money;

public class NotEnoughMoneyException extends  Exception{
    private double customerBalance;
    private double amountToPay;

    public NotEnoughMoneyException(double customerBalance, double amountToPay) {
        this.customerBalance = customerBalance;
        this.amountToPay = amountToPay;
    }

    @Override
    public String getMessage() {
        return "Not enough money in your account! you have " + customerBalance + " but you tried to pay" + amountToPay;
    }
}
