package dto.loanDTO;

import javafx.util.Pair;

import java.util.*;

public class LoanDTO {

    private final String id;
    private String owner;
    private final String category;
    private final double amount;
    private final int totalYaz;
    private final int paysEveryYaz;
    private final double interestPerPayment;
    private String status;
    private Map<String, Double> lenders;
    private List<String> lendersName;
    private List<Double> lendersAmount;
    private double amountRaised;
    private double amountLeftToActive;
    private int startingYaz;//p
    private int nextPaymentYaz;
    private List <Integer> paymentYaz;
    private List <Double> paymentAmount;
    private List<Double> paymentInterest;
    private List<Double> paymentAmountWithInterest;
    private double totalPaidAmount;
    private double totalPaidInterest;
    private double totalAmountLeftToPay;
    private double totalInterestLeftToPay;
    private Pair<Integer, Double> delayedPayments;
    private int finishYaz;
    private boolean isPayingPeriod;
    private double sellPrice;
    private double buyPrice;

    private String amountRaisedString;
    private String amountLeftActiveString;
    private String totalPaidAmountString;
    private String totalPaidInterestString;
    private String totalAmountLeftToPayString;
    private String totalInterestLeftToPayString;
    private String delayedPaymentsAmountString;
    private String buyPriceString;
    private String sellPriceString;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoanDTO loanDTO = (LoanDTO) o;
        return Double.compare(loanDTO.amount, amount) == 0 && totalYaz == loanDTO.totalYaz && paysEveryYaz == loanDTO.paysEveryYaz && Double.compare(loanDTO.interestPerPayment, interestPerPayment) == 0 && startingYaz == loanDTO.startingYaz && nextPaymentYaz == loanDTO.nextPaymentYaz && finishYaz == loanDTO.finishYaz && isPayingPeriod == loanDTO.isPayingPeriod && Double.compare(loanDTO.sellPrice, sellPrice) == 0 && Double.compare(loanDTO.buyPrice, buyPrice) == 0 && Objects.equals(id, loanDTO.id) && Objects.equals(owner, loanDTO.owner) && Objects.equals(category, loanDTO.category) && Objects.equals(status, loanDTO.status) && Objects.equals(lenders, loanDTO.lenders) && Objects.equals(paymentYaz, loanDTO.paymentYaz) && Objects.equals(paymentAmount, loanDTO.paymentAmount) && Objects.equals(paymentInterest, loanDTO.paymentInterest) && Objects.equals(delayedPayments, loanDTO.delayedPayments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, owner, category, amount, totalYaz, paysEveryYaz, interestPerPayment, status, lenders, startingYaz, nextPaymentYaz, paymentYaz, paymentAmount, paymentInterest, delayedPayments, finishYaz, isPayingPeriod, sellPrice, buyPrice);
    }

    public LoanDTO(String id, String owner, String category, double amount, int totalYaz, int paysEveryYaz, double interestPerPayment, String status, Map<String, Double> lenders,
                   int startingYaz, int nextPaymentYaz, List<Integer> paymentYaz, List<Double> paymentAmount, List<Double> paymentInterest, Pair<Integer, Double> delayedPayments, int finishYaz,
                   boolean isPayingPeriod) {
        this.id = id;
        this.owner = owner;
        this.category = category;
        this.amount = amount;
        this.totalYaz = totalYaz;
        this.paysEveryYaz = paysEveryYaz;
        this.interestPerPayment = interestPerPayment;
        this.status = status;
        this.lenders = lenders;
        this.startingYaz = startingYaz;
        this.nextPaymentYaz = nextPaymentYaz;
        this.paymentYaz = paymentYaz;
        this.paymentAmount = paymentAmount;
        this.paymentInterest = paymentInterest;
        this.delayedPayments = delayedPayments;
        this.finishYaz = finishYaz;
        this.isPayingPeriod=isPayingPeriod;
        lendersName=new ArrayList<>(lenders.keySet().size());
        lendersAmount=new ArrayList<>(lenders.keySet().size());
        for(String lender : lenders.keySet()) {
            lendersName.add(lender);
            lendersAmount.add(lenders.get(lender));
        }
        amountRaised=lenders.values().stream().mapToDouble(Double::doubleValue).sum();
        amountLeftToActive=amount-amountRaised;
        paymentAmountWithInterest=new ArrayList<>(paymentAmount.size());
        for(int i=0; i<paymentAmount.size(); i++) {
            paymentAmountWithInterest.add(paymentAmount.get(i)+paymentInterest.get(i));
        }
        totalPaidAmount=paymentAmount.stream().mapToDouble(Double::doubleValue).sum();
        totalPaidInterest=paymentInterest.stream().mapToDouble(Double::doubleValue).sum();
        totalAmountLeftToPay=amount-totalPaidAmount;
        totalInterestLeftToPay=(amount*interestPerPayment/100)-totalPaidInterest;

        amountRaisedString=String.format("%.1f",amountRaised);
        amountLeftActiveString=String.format("%.1f",amountLeftToActive);
        totalPaidAmountString=String.format("%.1f",totalPaidAmount);
        totalPaidInterestString=String.format("%.1f", totalPaidInterest);
        totalAmountLeftToPayString=String.format("%.1f", totalAmountLeftToPay);
        totalInterestLeftToPayString=String.format("%.1f", totalInterestLeftToPay);
        delayedPaymentsAmountString=String.format("%.1f", delayedPayments.getValue());

        sellPrice=0;
        buyPrice=0;
    }

    public LoanDTO(String id, String owner, String category, double amount, int totalYaz, int paysEveryYaz, double interestPerPayment, String status, Map<String, Double> lenders,
                   int startingYaz, int nextPaymentYaz, List<Integer> paymentYaz, List<Double> paymentAmount, List<Double> paymentInterest, Pair<Integer, Double> delayedPayments, int finishYaz,
                   boolean isPayingPeriod, double sellPrice, double buyPrice) {
            this(id, owner, category, amount, totalYaz, paysEveryYaz, interestPerPayment, status, lenders,
        startingYaz, nextPaymentYaz, paymentYaz, paymentAmount, paymentInterest, delayedPayments, finishYaz,
        isPayingPeriod);
            this.sellPrice=sellPrice;
            this.buyPrice=buyPrice;
        buyPriceString=String.format("%.1f", buyPrice);
        sellPriceString=String.format("%.1f", sellPrice);
    }


    public LoanDTO(String id, String category, double amount, int totalYaz, int paysEveryYaz, double interestPerPayment) {
        this.id = id;
        this.category = category;
        this.amount = amount;
        this.totalYaz = totalYaz;
        this.paysEveryYaz = paysEveryYaz;
        this.interestPerPayment = interestPerPayment;
    }

    public String getId() {
        return id;
    }

    public String getOwner() {
        return owner;
    }

    public String getCategory() {
        return category;
    }

    public double getAmount() {
        return amount;
    }

    public int getTotalYaz() {
        return totalYaz;
    }

    public int getPaysEveryYaz() {
        return paysEveryYaz;
    }

    public double getInterestPerPayment() {
        return interestPerPayment;
    }

    public String getStatus() {
        return status;
    }

    public Map<String, Double> getLenders() {
        return lenders;
    }

    public int getStartingYaz() {
        return startingYaz;
    }

    public int getNextPaymentYaz() {
        return nextPaymentYaz;
    }

    public List<Integer> getPaymentYaz() {
        return paymentYaz;
    }

    public List<Double> getPaymentAmount() {
        return paymentAmount;
    }

    public List<Double> getPaymentInterest() {
        return paymentInterest;
    }

    public Pair<Integer, Double> getDelayedPayments() {
        return delayedPayments;
    }

    public double getTotalMoney(){return amount +amount*(interestPerPayment/100); }

    public int getFinishYaz() {
        return finishYaz;
    }

    public double getPaidAmount() {return paymentAmount.stream().mapToDouble(Double::doubleValue).sum();}

    public double getPaidInterest(){ return paymentInterest.stream().mapToDouble(Double::doubleValue).sum();}

    public double getAmountLeftToPay() {return amount-getPaidAmount();}

    public double getInterestLeftToPay() {return (amount*interestPerPayment/100) - getPaidInterest();}

    public double getExpectedAmount() {return getTotalMoney() * getPaysEveryYaz() / getTotalYaz() ; }

    public List<String> getLendersName() {
        return lendersName;
    }

    public List<Double> getLendersAmount() {
        return lendersAmount;
    }

    public double getAmountRaised() {
        return amountRaised;
    }

    public double getAmountLeftToActive() {
        return amountLeftToActive;
    }

    public List<Double> getPaymentAmountWithInterest() {
        return paymentAmountWithInterest;
    }

    public double getTotalPaidAmount() {
        return totalPaidAmount;
    }

    public double getTotalPaidInterest() {
        return totalPaidInterest;
    }

    public double getTotalAmountLeftToPay() {
        return totalAmountLeftToPay;
    }

    public double getTotalInterestLeftToPay() {
        return totalInterestLeftToPay;
    }

    public String getAmountRaisedString() {
        return amountRaisedString;
    }

    public String getAmountLeftActiveString() {
        return amountLeftActiveString;
    }

    public String getTotalPaidAmountString() {
        return totalPaidAmountString;
    }

    public String getTotalPaidInterestString() {
        return totalPaidInterestString;
    }

    public String getTotalAmountLeftToPayString() {
        return totalAmountLeftToPayString;
    }

    public String getTotalInterestLeftToPayString() {
        return totalInterestLeftToPayString;
    }

    public String getDelayedPaymentsAmountString() {
        return delayedPaymentsAmountString;
    }

    public double getSellPrice() {
        return sellPrice;
    }

    public double getBuyPrice() {
        return buyPrice;
    }

    public String getBuyPriceString() {
        return buyPriceString;
    }

    public String getSellPriceString() {
        return sellPriceString;
    }

    public boolean isPayingPeriod() {
        return isPayingPeriod;
    }
}
