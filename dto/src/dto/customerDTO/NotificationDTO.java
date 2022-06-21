package dto.customerDTO;

public class NotificationDTO {
    private final String id;
    private final int yaz;
    private final double amount;
    private final String amountString;

    public NotificationDTO(String id, int yaz, double amount) {
        this.id = id;
        this.yaz = yaz;
        this.amount = amount;
        amountString=String.format("%.1f", amount);
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

    public String getAmountString() {
        return amountString;
    }
}
