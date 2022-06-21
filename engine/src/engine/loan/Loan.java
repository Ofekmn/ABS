package engine.loan;


import javafx.util.Pair;

import java.io.Serializable;
import java.util.*;

public class Loan implements Comparable<Loan>, Serializable {
    private final String id;
    private final String owner;
    private final double amount;
    private final int totalYaz; //the total yaz the loan will take after becoming active
    private final int paysEveryXYaz;
    private final double interestPerPayment;
    private final String category;
    ////////////////////////////////////////////////////////
    private Map<String, Double> lenders=new LinkedHashMap<>();

    private int startingYaz; //command 6
    private double amountPaid;   //command 7
    private double interestPaid; //command 7
    private String status;
    private int yazCount; //IF ACTIVE: ADVANCE BY 1 ALONG WITH COMMAND 7.
    private List<Payment> payments=new LinkedList<>(); //command 7
    private Pair<Integer, Double> delayedPayments;//command 7
    private int finishYaz;//command 7
    private double totalAmountToPayForPayment;
    private boolean isPayingPeriod =false;



    public Loan(String id, String owner, double amount, int totalYaz, int paysEveryXYaz, double interestPerPayment, String category) {
        this.id = id;
        this.owner = owner;
        this.amount = amount;
        this.totalYaz = totalYaz;
        this.paysEveryXYaz = paysEveryXYaz;
        this.interestPerPayment = interestPerPayment;
        this.category = category;
        totalAmountToPayForPayment=0;
        status = "new";
        delayedPayments=new Pair<>(0, 0.0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Loan loan = (Loan) o;
        return Objects.equals(id, loan.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public String getId() {
        return id;
    }


    public String getOwner() {
        return owner;
    }


    public double getAmount() {
        return amount;
    }

    public int getTotalYaz() {
        return totalYaz;
    }

    public int getPaysEveryXYaz() {
        return paysEveryXYaz;
    }


    public double getInterestPerPayment() {
        return interestPerPayment;
    }


    public String getCategory() {
        return category;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Map<String, Double> getLenders() {
        return lenders;
    }


    public int getStartingYaz() {
        return startingYaz;
    }

    public void setStartingYaz(int startingYaz) {
        this.startingYaz = startingYaz;
    }

    public int getNextYazPayment(){return paysEveryXYaz - yazCount;}


    public List<Payment> getPayments() {
        return payments;
    }


    public int getFinishYaz() {
        return finishYaz;
    }

    public void setFinishYaz(int finishYaz) {
        this.finishYaz = finishYaz;
    }

    public double getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(double amountPaid) {
        this.amountPaid = amountPaid;
    }

    public double getInterestPaid() {
        return interestPaid;
    }

    public void setInterestPaid(double interestPaid) {
        this.interestPaid = interestPaid;
    }

    public int getYazCount() {
        return yazCount;
    }

    public void setYazCount(int yazCount) {
        this.yazCount = yazCount;
    }

    public double getTotalAmountToPayForPayment() {
        return totalAmountToPayForPayment;
    }

    public void setTotalAmountToPayForPayment(double totalAmountToPayForPayment) {
        this.totalAmountToPayForPayment = totalAmountToPayForPayment;
    }

    public boolean isPayingPeriod() {
        return isPayingPeriod;
    }

    public void setPayingPeriod(boolean payingPeriod) {
        isPayingPeriod = payingPeriod;
    }

    public int nextPaymentYaz(int currentYaz) {
        return currentYaz+paysEveryXYaz-yazCount;
    }

    public double singePaymentAmount(){return amount * paysEveryXYaz/totalYaz; }

    public double singlePaymentInterest(){return (amount*interestPerPayment/100) *paysEveryXYaz/totalYaz; }

    public double singePaymentTotal() {return singePaymentAmount() + singlePaymentInterest();}

    public Pair<Integer, Double> getDelayedPayments() {
        return delayedPayments;
    }

    public void setDelayedPayments(Pair<Integer, Double> delayedPayments) {
        this.delayedPayments = delayedPayments;
    }

    public double  amountLeftToBecomeActive(){return amount - lenders.values().stream().mapToDouble(Double::doubleValue).sum();}

    public double totalInterest() {return amount*interestPerPayment/100 ;}

    public double totalSum() {return amount + interestPerPayment*amount/100;}



    @Override
    public int compareTo(Loan loan){
        if(startingYaz!=loan.startingYaz)
            return  startingYaz-loan.startingYaz;
        else return (int)(amount-loan.amount);
    }
    public int compareByAmount(Loan loan) {
        return (int)(amountLeftToBecomeActive()- loan.amountLeftToBecomeActive());
    }
}

