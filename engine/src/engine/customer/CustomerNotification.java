package engine.customer;

public class CustomerNotification {
    private final String id;
    private final int yaz;
    private final double amount;

    public CustomerNotification(String id, int yaz, double amount) {
        this.id = id;
        this.yaz = yaz;
        this.amount = amount;
    }

    public String getId() {
        return id;
    }

    public int getYaz() {
        return yaz;
    }

    public double getAmount() {
        return amount;
    }
}
