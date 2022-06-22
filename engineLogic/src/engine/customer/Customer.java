package engine.customer;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Customer implements Serializable {
    private String name;
    private double balance;
    private List<Transaction> transactions;
    private List<String> borrowerLoans;
    private List<String> lenderLoans;
    private List<CustomerNotification> notificationList;


    public Customer() {
    }

    public Customer(String name, double balance) {
        borrowerLoans=new LinkedList<>();
        lenderLoans=new LinkedList<>();
        transactions=new LinkedList<>();
        notificationList= new LinkedList<>();
        this.name = name;
        this.balance = balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return Objects.equals(name, customer.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getBorrowerLoans() {
        return borrowerLoans;
    }

    public void setBorrowerLoans(List<String> borrowerLoans) {
        this.borrowerLoans = borrowerLoans;
    }

    public List<String> getLenderLoans() {
        return lenderLoans;
    }

    public void setLenderLoans(List<String> lenderLoans) {
        this.lenderLoans = lenderLoans;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public List<CustomerNotification> getNotificationList() {
        return notificationList;
    }
}
