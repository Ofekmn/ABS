package engine;

import dto.customerDTO.CustomerDTO;
import dto.customerDTO.NotificationDTO;
import dto.customerDTO.TransactionDTO;
import dto.loanDTO.LoanDTO;
import engine.customer.*;
import engine.exception.xml.LoanFieldDoesNotExist;
import engine.exception.xml.NameException;
import engine.exception.xml.YazException;
import engine.generated.*;
import engine.loan.*;
import engine.old.generated.AbsCustomer;
import engine.old.generated.AbsDescriptor;
import engine.old.generated.AbsLoan;
import javafx.util.Pair;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.copy;

public class EngineImpl implements Engine, Serializable  {

    private int currentYaz;
    private boolean isFileLoaded;
    private Set<String> possibleCategories;
    private Map<String,Customer> customers;
    private Map<String,Loan> loans;


    private final static String JAXB_XML_PACKAGE_NAME = "engine.generated";

    public EngineImpl(){
        currentYaz=1;
        isFileLoaded=false;
        possibleCategories=new HashSet<>();
        customers=new HashMap<>();
        loans=new HashMap<>();
    }


    @Override
    public int getCurrentYaz(){return currentYaz;}
    @Override
    public void loadFile(InputStream file, String name) throws NameException, LoanFieldDoesNotExist, YazException {

        try {
            AbsDescriptor descriptor = deserializeFrom(file);

            Set<String> tempCategories = new HashSet<>(possibleCategories);
            for (String absCategory : descriptor.getAbsCategories().getAbsCategory()) {
                tempCategories.add(absCategory.trim());
            }
            Set<String> lowerCaseCategories = tempCategories.stream().map(String::toLowerCase).collect(Collectors.toSet());
            Map<String, Loan> tempLoans = new HashMap<>(loans);
            Set<String> lowerCaseLoans = loans.keySet().stream().map(String::toLowerCase).collect(Collectors.toSet());
            for (AbsLoan absLoan : descriptor.getAbsLoans().getAbsLoan()) {
                Loan tempLoan=new Loan(absLoan.getId().trim(), name.trim(),
                        absLoan.getAbsCapital(),absLoan.getAbsTotalYazTime(), absLoan.getAbsPaysEveryYaz(),
                        absLoan.getAbsIntristPerPayment(), absLoan.getAbsCategory().trim());


                if(!lowerCaseCategories.contains(tempLoan.getCategory().toLowerCase(Locale.ROOT))) {
                    throw new LoanFieldDoesNotExist(tempLoan.getId() ,"category", tempLoan.getCategory());
                }
                if(tempLoan.getTotalYaz()% tempLoan.getPaysEveryXYaz()!=0){
                    throw new YazException(tempLoan.getId(), tempLoan.getTotalYaz(), tempLoan.getPaysEveryXYaz());
                }
                if(lowerCaseLoans.contains(tempLoan.getId().toLowerCase(Locale.ROOT))) {
                    throw new NameException("loan", tempLoan.getId());
                }
                lowerCaseLoans.add(absLoan.getId().trim().toLowerCase(Locale.ROOT));
                tempLoans.put(tempLoan.getId(),tempLoan);
            }
            for(AbsLoan absLoan: descriptor.getAbsLoans().getAbsLoan()) {
                customers.get(name).getBorrowerLoans().add(absLoan.getId());
            }
            possibleCategories=tempCategories;
            loans=tempLoans;
        }
        catch(JAXBException  ignored){

        }
    }

    @Override
    public void loadFileOld(File xmlFile) throws NameException, LoanFieldDoesNotExist, YazException {

        try {
            InputStream file = new FileInputStream(xmlFile);
            AbsDescriptor descriptor = deserializeFrom(file);
            Set<String> tempCategories = new HashSet<>();
            Set<String> lowerCaseCategories= new HashSet<>();
            for (String absCategory:descriptor.getAbsCategories().getAbsCategory()) {
                tempCategories.add(absCategory.trim());
                lowerCaseCategories.add(absCategory.trim().toLowerCase(Locale.ROOT));
            }
            Map<String, Customer> tempCustomers = new HashMap<>();
            Set<String> lowerCaseCustomers= new HashSet<>();
            for (AbsCustomer absCustomer : descriptor.getAbsCustomers().getAbsCustomer()) {
                Customer tempCustomer = new Customer(absCustomer.getName().trim(), absCustomer.getAbsBalance());
                if (lowerCaseCustomers.contains(tempCustomer.getName().toLowerCase(Locale.ROOT))) {
                    throw new NameException("customer", tempCustomer.getName());
                }
                lowerCaseCustomers.add(absCustomer.getName().trim().toLowerCase(Locale.ROOT));
                tempCustomers.put(tempCustomer.getName(), tempCustomer);
            }
            Map<String,Loan> tempLoans = new HashMap<>();
            Set<String> lowerCaseLoans= new HashSet<>();
            for (AbsLoan absLoan : descriptor.getAbsLoans().getAbsLoan()) {
                Loan tempLoan=new Loan(absLoan.getId().trim(), absLoan.getAbsOwner().trim(),
                        absLoan.getAbsCapital(),absLoan.getAbsTotalYazTime(), absLoan.getAbsPaysEveryYaz(),
                        absLoan.getAbsIntristPerPayment(), absLoan.getAbsCategory().trim());


                if(!lowerCaseCategories.contains(tempLoan.getCategory().toLowerCase(Locale.ROOT))) {
                    throw new LoanFieldDoesNotExist(tempLoan.getId() ,"category", tempLoan.getCategory());
                }
                if(!lowerCaseCustomers.contains(tempLoan.getOwner().toLowerCase(Locale.ROOT))){
                    throw new LoanFieldDoesNotExist(tempLoan.getId(), "customer", tempLoan.getOwner());
                }
                if(tempLoan.getTotalYaz()% tempLoan.getPaysEveryXYaz()!=0){
                    throw new YazException(tempLoan.getId(), tempLoan.getTotalYaz(), tempLoan.getPaysEveryXYaz());
                }
                if(lowerCaseLoans.contains(tempLoan.getId().toLowerCase(Locale.ROOT))) {
                    throw new NameException("loan", tempLoan.getId());
                }
                lowerCaseLoans.add(absLoan.getId().trim().toLowerCase(Locale.ROOT));
                tempCustomers.get(tempLoan.getOwner()).getBorrowerLoans().add(tempLoan.getId());
                tempLoans.put(tempLoan.getId(),tempLoan);
            }
            isFileLoaded=true;
            currentYaz=1;
            possibleCategories=tempCategories;
            loans=tempLoans;
            customers=tempCustomers;

        }
        catch(JAXBException | FileNotFoundException ignored){

        }
    }

    private AbsDescriptor deserializeFrom(InputStream in) throws JAXBException {
        JAXBContext jc= JAXBContext.newInstance(JAXB_XML_PACKAGE_NAME);
        Unmarshaller u=jc.createUnmarshaller();
        return (AbsDescriptor) u.unmarshal((in));
    }

    public void setCustomers(Map<String, Customer> customers) {
        this.customers = customers;
    }

    @Override
    public boolean isFileLoaded() {return !isFileLoaded;}

    @Override
    public List<LoanDTO> getAllLoans(){
        List<LoanDTO> DTOList=new LinkedList<>();
        for (Loan loan: loans.values()) {
            LoanDTO loanDTO=createLoanDTO(loan);
            DTOList.add(loanDTO);
        }

        return DTOList;
    }

    @Override
    public List<CustomerDTO> getAllCustomersDetails() {
        List <CustomerDTO> customerDTOList=new LinkedList<>();

        for (Customer customer :customers.values()){
            CustomerDTO customerDTO=createCustomerDTO(customer);
            customerDTOList.add(customerDTO);
        }
        return customerDTOList;
    }
    private LoanDTO createLoanDTO(Loan loan){
        return new LoanDTO(loan.getId(), loan.getOwner(), loan.getCategory(),loan.getAmount(), loan.getTotalYaz(),
                loan.getPaysEveryXYaz(), loan.getInterestPerPayment(), loan.getStatus(), loan.getLenders(), loan.getStartingYaz(),//p
                loan.nextPaymentYaz(currentYaz), loan.getPayments().stream().map(Payment::getYaz).collect(Collectors.toCollection(ArrayList::new)),
                loan.getPayments().stream().map(Payment::getAmount).collect(Collectors.toCollection(ArrayList::new)),
                loan.getPayments().stream().map(Payment::getInterest).collect(Collectors.toCollection(ArrayList::new)),//a
                loan.getDelayedPayments(), loan.getFinishYaz(), loan.isPayingPeriod());//r+f
    }

    private CustomerDTO createCustomerDTO(Customer customer) {
        List <LoanDTO> ownerDTOList=new LinkedList<>();
        for(String string: customer.getBorrowerLoans()){
            Loan loan=loans.get(string);
            ownerDTOList.add(createLoanDTO(loan));
        }
        List <LoanDTO> lenderDTOList=new LinkedList<>();
        for(String string: customer.getLenderLoans()){
            lenderDTOList.add(createLoanDTO(loans.get(string)));
        }
        List<TransactionDTO> transactionDTOList= new ArrayList<>(customer.getTransactions().size());
        for (Transaction transaction : customer.getTransactions()) {
            transactionDTOList.add(createTransactionDTO(transaction));
        }
        List<NotificationDTO> notificationDTOList= new ArrayList<>(customer.getNotificationList().size());
        for(CustomerNotification notification : customer.getNotificationList()){
            notificationDTOList.add(createNotificationDTO(notification));
        }
        return new CustomerDTO(customer.getName(), customer.getBalance(), transactionDTOList , ownerDTOList, lenderDTOList,
                notificationDTOList);
    }

    private TransactionDTO createTransactionDTO(Transaction transaction){
        return new TransactionDTO(transaction.getYaz(), transaction.getSign(),transaction.getPrice() ,transaction.getAmountBeforeTransaction(), transaction.getAmountAfterTransaction());
    }

    private NotificationDTO createNotificationDTO(CustomerNotification notification) {
        return new NotificationDTO(notification.getId(), notification.getYaz(), notification.getAmount());
    }

    public CustomerDTO createCustomerDTO(String name) {
        return createCustomerDTO(customers.get(name));
    }

    public LoanDTO createLoanDTO(String Id) {
        return createLoanDTO(loans.get(Id));
    }

    public Map<String,Customer> getCustomersMap(){
        return customers;
    }

    @Override
    public List<Pair<String, Double>> getCustomers(){
        List<Pair<String, Double>> namesAmountPairs=new LinkedList<>();
        for ( Customer customer : customers.values()) {
            Pair<String, Double> pair=new Pair<>(customer.getName(), customer.getBalance());
            namesAmountPairs.add(pair);
        }
        return namesAmountPairs;
    }
    @Override
    public void charge(String name, double amount){

        customers.get(name).getTransactions().add(0,new Transaction(currentYaz, amount, '+',customers.get(name).getBalance(), customers.get(name).getBalance()+amount));
        customers.get(name).setBalance(customers.get(name).getBalance()+amount);
    }
    @Override
    public void withdraw(String name, double amount){
        customers.get(name).getTransactions().add(0,new Transaction(currentYaz, amount, '-',customers.get(name).getBalance(), customers.get(name).getBalance()-amount));
        customers.get(name).setBalance(customers.get(name).getBalance()-amount);
    }

    public List<String> getCategories() {
        return new ArrayList<>(possibleCategories);
    }

    public List<LoanDTO>  filteredLoans(String name, Set<String> categories, double interest, int totalYaz, int maximumOpenLoans) {
        List<LoanDTO> filteredLoansList=new LinkedList<>();
        for(Loan loan : loans.values() ){
            if(((!loan.getOwner().equals(name)) && (loan.getStatus().equals("new") ||
                    loan.getStatus().equals("pending"))) && (categories.isEmpty() || categories.contains(loan.getCategory()))
            && (interest<= loan.getInterestPerPayment()) && (totalYaz <= loan.getTotalYaz())  &&
            customers.get(loan.getOwner()).getBorrowerLoans().stream()
                    .filter(f-> !loan.getStatus().equals("finished")).
                    count()<=maximumOpenLoans) {
                filteredLoansList.add(createLoanDTO(loan));
            }
        }
        return filteredLoansList;
    }


    public double assign(List<String> loanID, int amount, String customerName, int maximumLoanOwnership) {
        List<Loan> filteredLoansList=new ArrayList<>(loanID.size());
        for(String id : loanID){
            filteredLoansList.add(loans.get(id));
        }
        filteredLoansList.sort(Loan::compareByAmount); //list is sorted by amount left from lowest to highest
        int count=0;
        double totalAmountPaid=0;
        for (Loan loan : filteredLoansList) {

            double payToLoan=Math.min((amount - totalAmountPaid)/(loanID.size()-count), loan.getAmount()*maximumLoanOwnership/100);
            if (loan.amountLeftToBecomeActive() <= payToLoan) { //therefore, the loan will become active.
                double paid = loan.amountLeftToBecomeActive();
                loan.setStatus("active");
                loan.setStartingYaz(currentYaz);
                loan.setYazCount(0);
                if (loan.getLenders().containsKey(customerName)) {
                    loan.getLenders().replace(customerName, loan.getLenders().get(customerName) + paid);
                } else {
                    loan.getLenders().put(customerName, paid);
                    customers.get(customerName).getLenderLoans().add(loan.getId());
                }
                charge(loan.getOwner(), loan.getAmount()); //take out the amount of the loan from the owner's account.
                withdraw(customerName, paid);
                totalAmountPaid += paid;
            } else {
                if (loan.getStatus().equals("new"))
                    loan.setStatus("pending");
                if (loan.getLenders().containsKey(customerName)) {
                    loan.getLenders().replace(customerName, loan.getLenders().get(customerName) + payToLoan);
                } else {
                    loan.getLenders().put(customerName, payToLoan);
                    customers.get(customerName).getLenderLoans().add(loan.getId());
                }
                totalAmountPaid += payToLoan;
                withdraw(customerName, payToLoan);
            }
            ++count;
        }
        return totalAmountPaid;
    }




    public void advanceTime(){

        ++currentYaz;
        for(Customer customer :customers.values()) {
            for(String id: customer.getBorrowerLoans()) {
                Loan loan = loans.get(id);
                if (loan.getStatus().equals("active") || loan.getStatus().equals("risk")) {
                    loan.setYazCount(loan.getYazCount() + 1);
                    if (loan.isPayingPeriod() && loan.getStatus().equals("active")) { //if the loan missed the paying period
                        loan.setStatus("risk");
                        missedPayment(loan);
                    }
                    if (loan.getNextYazPayment() == 0 && loan.getStatus().equals("active")) {
                        double amount = loan.getAmount() * loan.getPaysEveryXYaz() / loan.getTotalYaz();
                        double interest = loan.getAmount() * (loan.getInterestPerPayment() / 100) * loan.getPaysEveryXYaz() / loan.getTotalYaz();
                        customer.getNotificationList().add(0, new CustomerNotification(id, currentYaz, amount + interest));
                        loan.setTotalAmountToPayForPayment(loan.getTotalAmountToPayForPayment()+amount+interest);
                        loan.setPayingPeriod(true); //The current Yaz is the paying period
                        loan.setYazCount(0);
                    } else if (loan.getStatus().equals("risk")) {
                        if(loan.isPayingPeriod() && (loan.getInterestPaid() +loan.getAmountPaid() + loan.getDelayedPayments().getValue()) != loan.totalSum()){
                            missedPayment(loan);
                        }
                        if (loan.getNextYazPayment() == 0) {
                            if ((loan.getAmountPaid() + loan.getDelayedPayments().getValue()) != loan.totalSum()) {
                                double amount = loan.getAmount() * loan.getPaysEveryXYaz() / loan.getTotalYaz();
                                double interest = loan.getAmount() * (loan.getInterestPerPayment() / 100) * loan.getPaysEveryXYaz() / loan.getTotalYaz();
                                loan.setTotalAmountToPayForPayment(loan.getTotalAmountToPayForPayment()+amount+interest);
                                customer.getNotificationList().add(0,new CustomerNotification(id, currentYaz, amount + interest + loan.getDelayedPayments().getValue()));
                                loan.setPayingPeriod(true);
                            } else {
                                customer.getNotificationList().add(0,new CustomerNotification(id, currentYaz, loan.getDelayedPayments().getValue()));
                            }
                            loan.setYazCount(0);
                        } else {
                            customer.getNotificationList().add(0, new CustomerNotification(id, currentYaz, loan.getDelayedPayments().getValue()));
                        }
                    }
                }
            }
        }
    }

    //TODO:: Check for weird FP bugs...

    public void closeLoan(String loanID) {
        Loan loan=loans.get(loanID);
        double amountToPay=loan.getAmount()-loan.getAmountPaid();
        double interestToPay=loan.totalInterest();
        outOfRisk(loan, amountToPay, interestToPay);
        loan.setStatus("finished");
        loan.setFinishYaz(currentYaz);
        loan.setPayingPeriod(false);
        loan.setDelayedPayments(new Pair<>(0, 0.0));

    }

    private void outOfRisk(Loan loan, double amountToPay, double interestToPay) {
        payToLenders(loan, amountToPay, interestToPay);
        loan.setTotalAmountToPayForPayment(0);
        loan.getPayments().add(new Payment(currentYaz, amountToPay, interestToPay));
        loan.setAmountPaid(loan.getAmountPaid()+amountToPay);
        loan.setInterestPaid(loan.getInterestPaid()+interestToPay);
    }

    private void payToLenders(Loan loan, double amountToPay, double interestToPay) {
        for(Map.Entry<String, Double> lenderEntry : loan.getLenders().entrySet()) {
            double lenderOwnership=lenderEntry.getValue()/loan.getAmount();
            double amount=amountToPay*lenderOwnership;
            double interest=interestToPay*lenderOwnership;
            charge(lenderEntry.getKey(), amount + interest);
            withdraw(loan.getOwner(), amount+interest);
        }
    }

    public void payLoanRisk(String loanID, double totalSum) {
        Loan loan=loans.get(loanID);
        double amountToPay=totalSum/(1+loan.getInterestPerPayment()/100);
        double interestToPay=totalSum-amountToPay;
        payToLenders(loan, amountToPay, interestToPay);
        loan.setTotalAmountToPayForPayment(loan.getTotalAmountToPayForPayment()-totalSum);
        loan.getPayments().add(new Payment(currentYaz, amountToPay, interestToPay));
        loan.setAmountPaid(loan.getAmountPaid()+amountToPay);
        loan.setInterestPaid(loan.getInterestPaid()+interestToPay);
        BigDecimal loanAmount= BigDecimal.valueOf(loan.getAmount());
        BigDecimal amountPaid= BigDecimal.valueOf(loan.getAmountPaid());
        loanAmount=loanAmount.round(new MathContext(2));
        amountPaid=amountPaid.round(new MathContext(2));
        BigDecimal totalPaidForPayment= BigDecimal.valueOf(loan.getTotalAmountToPayForPayment());
        totalPaidForPayment=totalPaidForPayment.round(new MathContext(2));
        if(loanAmount.equals(amountPaid)) { // casting for weird floating point cases
            loan.setStatus("finished");
            loan.setFinishYaz(currentYaz);
            loan.setPayingPeriod(false);
            loan.setDelayedPayments(new Pair<>(0, 0.0));
        }

        else if(totalPaidForPayment.intValue()==0) {
            loan.setStatus("active");
            loan.setPayingPeriod(false);
            loan.setDelayedPayments(new Pair<>(0, 0.0));
        }
        else {
            loan.setDelayedPayments(new Pair<>(loan.getDelayedPayments().getKey(), loan.getDelayedPayments().getValue()-totalSum));
            int delayedAmount=loan.getDelayedPayments().getValue().intValue()/((int)loan.singePaymentTotal());
            if((loan.getDelayedPayments().getValue().intValue()%((int)loan.singePaymentTotal()))==0)
                delayedAmount++;
            if(delayedAmount==0)
                loan.setDelayedPayments(new Pair<>(delayedAmount, 0.0));
            else
                loan.setDelayedPayments(new Pair<>(delayedAmount, loan.getDelayedPayments().getValue()));
        }
    }

    public void payLoan(String loanID) {
        Loan loan=loans.get(loanID);
        if(loan.getStatus().equals("active")) {
            for(Map.Entry<String, Double> lenderEntry : loan.getLenders().entrySet()) {
                double lenderOwnership=lenderEntry.getValue()/loan.getAmount();
                double amount=loan.singePaymentAmount()*lenderOwnership;
                double interest=loan.singlePaymentInterest()*lenderOwnership;
                charge(lenderEntry.getKey(), amount + interest);
                withdraw(loan.getOwner(), amount+interest);
            }
            loan.setTotalAmountToPayForPayment(0);
            loan.getPayments().add(new Payment(currentYaz, loan.singePaymentAmount(), loan.singlePaymentInterest()));
            loan.setAmountPaid(loan.getAmountPaid()+loan.singePaymentAmount());
            loan.setInterestPaid(loan.getInterestPaid()+loan.singlePaymentInterest());
            BigDecimal loanAmount= BigDecimal.valueOf(loan.getAmount());
            BigDecimal amountPaid= BigDecimal.valueOf(loan.getAmountPaid());
            loanAmount=loanAmount.round(new MathContext(2));
            amountPaid=amountPaid.round(new MathContext(2));
            if(loanAmount.equals(amountPaid)) { // casting for weird floating point cases
                loan.setStatus("finished");
                loan.setFinishYaz(currentYaz);
            }
            loan.setPayingPeriod(false);
        }
        else if(loan.getStatus().equals("risk")) {
            double entirePaymentAmount=loan.getTotalAmountToPayForPayment()/(1+loan.getInterestPerPayment()/100);
            double entirePaymentInterest=loan.getTotalAmountToPayForPayment() - entirePaymentAmount;
            outOfRisk(loan, entirePaymentAmount, entirePaymentInterest);
            if((int)loan.getAmount()==(int)loan.getAmountPaid()) { // casting for weird floating point cases
                loan.setStatus("finished");
                loan.setFinishYaz(currentYaz);
            }
            else
                loan.setStatus("active");
            loan.setPayingPeriod(false);
            loan.setDelayedPayments(new Pair<>(0, 0.0));
        }
    }

    private void missedPayment(Loan loan) {
        Pair<Integer, Double> pair = new Pair<>(loan.getDelayedPayments().getKey() + 1, (loan.getDelayedPayments().getValue() +loan.singePaymentTotal()));
        loan.setDelayedPayments(pair);
        loan.setPayingPeriod(false);
    }
//        for(Customer customer: customers.values()){
//            SortedSet <Loan> sortedLoans=new TreeSet<>(Loan::compareTo);
//            for(String id : customer.getBorrowerLoans())
//                sortedLoans.add(loans.get(id));
//            for(Loan loan: sortedLoans){
//                if(loan.getStatus().equals("active") || loan.getStatus().equals("risk")) {
//                    loan.setYazCount(loan.getYazCount()+1);
//                    if(loan.getNextYazPayment()==0) {
//                        double amount=loan.getAmount() * loan.getPaysEveryXYaz() / loan.getTotalYaz();
//                        double interest= loan.getAmount() * (loan.getInterestPerPayment()/100) * loan.getPaysEveryXYaz() / loan.getTotalYaz();
//                        if( (amount + interest) <=customer.getBalance()){
//                            for(Map.Entry<String, Double> lender : loan.getLenders().entrySet()){
//                                charge(lender.getKey(), (lender.getValue() + (lender.getValue()*(loan.getInterestPerPayment()/100))) * loan.getPaysEveryXYaz()/ loan.getTotalYaz());
//                                withdraw(loan.getOwner(), (lender.getValue() + (lender.getValue()*(loan.getInterestPerPayment()/100))) * loan.getPaysEveryXYaz()/ loan.getTotalYaz());
//                            }
//                            loan.getPayments().add(new Payment(currentYaz, amount, interest));
//                            loan.setAmountPaid(loan.getAmountPaid() + amount);
//                            loan.setInterestPaid(loan.getInterestPaid() + interest);
//                            if(loan.getStatus().equals("risk")) {
//                                Pair<Integer, Double> pair=new Pair<>(loan.getDelayedPayments().getKey()-1, loan.getDelayedPayments().getValue() - (amount + interest));
//                                loan.setDelayedPayments(pair);
//
//                                if(loan.getDelayedPayments().getKey()==0)
//                                    loan.setStatus("active");
//                            }
//                                if(loan.getAmountPaid()==loan.getAmount()) {
//                                    loan.setStatus("finished");
//                                    loan.setFinishYaz(currentYaz);
//                                }
//
//                        } else{
//                            loan.setStatus("risk");
//                            if(!loan.getDelayedPayments().getKey().equals(loan.getTotalYaz()/loan.getPaysEveryXYaz() - loan.getPayments().size())){
//                                Pair<Integer, Double> pair=new Pair<>(loan.getDelayedPayments().getKey()+1 ,(loan.getDelayedPayments().getValue() + (loan.getAmount()+loan.getAmount()*(loan.getInterestPerPayment()/100))*loan.getPaysEveryXYaz()/ loan.getTotalYaz()));
//                                loan.setDelayedPayments(pair);
//                            }
//
//                        }
//                        loan.setYazCount(0);
//                    }
//                }
//            }
//        }


}

    //TODO:: Remember to add setPayingPeriod False To Payments


