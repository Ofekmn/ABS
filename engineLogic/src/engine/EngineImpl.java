package engine;

import dto.customerDTO.CustomerDTO;
import dto.customerDTO.NotificationDTO;
import dto.customerDTO.TransactionDTO;
import dto.database.AdminDatabase;
import dto.loanDTO.LoanDTO;
import engine.customer.*;
import engine.exception.money.NotEnoughMoneyException;
import engine.exception.xml.LoanFieldDoesNotExist;
import engine.exception.xml.NameException;
import engine.exception.xml.YazException;
import engine.generated.*;
import engine.loan.*;
import javafx.util.Pair;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.*;
import java.util.stream.Collectors;

public class EngineImpl implements Engine, Serializable  {

    private int currentYaz;
    private boolean isFileLoaded;
    private Set<String> possibleCategories;
    private Map<String,Customer> customers;
    private Map<String,Loan> loans;
    private Set<Loan> loansForSale;
    private Map<Integer, Pair<AdminDatabase, Map<String, CustomerDTO>>> history;
    private int rewindYaz;
    private boolean isRewind;


    private final static String JAXB_XML_PACKAGE_NAME = "engine.generated";

    public EngineImpl(){
        currentYaz=1;
        isFileLoaded=false;
        possibleCategories=new HashSet<>();
        customers=new HashMap<>();
        loans=new HashMap<>();
        loansForSale=new HashSet<>();
        isRewind=false;
        history=new HashMap<>();
    }

    public int getRewindStateYaz(){
        if (isRewind)
            return rewindYaz;
        return currentYaz;
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
        catch(JAXBException e){
            e.printStackTrace();
        }
    }

    public void createNewLoan(String ownerName, String loanID, int amount, String category, int payEveryYaz, int interest, int totalYaz) throws NameException, YazException {
        Loan loan= new Loan(loanID, ownerName, amount, totalYaz, payEveryYaz, interest, category);
        if(loan.getTotalYaz()% loan.getPaysEveryXYaz()!=0){
            throw new YazException(loan.getId(), loan.getTotalYaz(), loan.getPaysEveryXYaz());
        }
        Set<String> lowerCaseLoans = loans.keySet().stream().map(String::toLowerCase).collect(Collectors.toSet());
        if(lowerCaseLoans.contains(loanID.trim().toLowerCase(Locale.ROOT)))
            throw new NameException("loan", loan.getId());
        Set<String> lowerCaseCategories = possibleCategories.stream().map(String::toLowerCase).collect(Collectors.toSet());
        if(!lowerCaseCategories.contains(category.trim().toLowerCase(Locale.ROOT)))
            possibleCategories.add(category);
        customers.get(ownerName).getBorrowerLoans().add(loanID);
        loans.put(loanID, loan);
    }

    @Override
    public void loadFileOld(InputStream file) throws NameException, LoanFieldDoesNotExist, YazException {
        return;
    }

    private AbsDescriptor deserializeFrom(InputStream in) throws JAXBException {
            JAXBContext jc= JAXBContext.newInstance("engine.generated");
        //JAXB_XML_PACKAGE_NAME
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
        if(isRewind)
            return history.get(rewindYaz).getKey().getLoanList();
        List<LoanDTO> DTOList=new LinkedList<>();
        for (Loan loan: loans.values()) {
            LoanDTO loanDTO=createLoanDTO(loan);
            DTOList.add(loanDTO);
        }
        return DTOList;
    }

    @Override
    public List<CustomerDTO> getAllCustomersDetails() {
        if(isRewind)
            return history.get(rewindYaz).getKey().getCustomerList();
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

    private LoanDTO createLoanDTO(Loan loan, double sellPrice, double buyPrice){

        return new LoanDTO(loan.getId(), loan.getOwner(), loan.getCategory(),loan.getAmount(), loan.getTotalYaz(),
                loan.getPaysEveryXYaz(), loan.getInterestPerPayment(), loan.getStatus(), loan.getLenders(), loan.getStartingYaz(),//p
                loan.nextPaymentYaz(currentYaz), loan.getPayments().stream().map(Payment::getYaz).collect(Collectors.toCollection(ArrayList::new)),
                loan.getPayments().stream().map(Payment::getAmount).collect(Collectors.toCollection(ArrayList::new)),
                loan.getPayments().stream().map(Payment::getInterest).collect(Collectors.toCollection(ArrayList::new)),//a
                loan.getDelayedPayments(), loan.getFinishYaz(), loan.isPayingPeriod(), sellPrice, buyPrice);//r+f
    }

    private CustomerDTO createCustomerDTO(Customer customer) {
        if(isRewind)
            return history.get(rewindYaz).getValue().get(customer.getName());
        List <LoanDTO> ownerDTOList=new LinkedList<>();
        for(String string: customer.getBorrowerLoans()){
            Loan loan=loans.get(string);
            ownerDTOList.add(createLoanDTO(loan));
        }
        List <LoanDTO> lenderDTOList=new LinkedList<>();
        List <LoanDTO> loansToSell=new LinkedList<>();
        for(String string: customer.getLenderLoans()){
            LoanDTO loanDTO=createLoanDTO(loans.get(string), loans.get(string).getSellPrice(customer.getName()), 0);
            lenderDTOList.add(loanDTO);
            Loan loan=loans.get(string);
            if(loan.getStatus().equals("active") && !loan.getSellers().contains(customer.getName()))
                loansToSell.add(loanDTO);
        }
        List<TransactionDTO> transactionDTOList= new ArrayList<>(customer.getTransactions().size());
        for (Transaction transaction : customer.getTransactions()) {
            transactionDTOList.add(createTransactionDTO(transaction));
        }
        List<NotificationDTO> notificationDTOList= new ArrayList<>(customer.getNotificationList().size());
        for(CustomerNotification notification : customer.getNotificationList()){
            notificationDTOList.add(createNotificationDTO(notification));
        }
        List<LoanDTO> loansToBuy=new LinkedList<>();
        for(Loan loanForSale : loansForSale) {
            if(!loanForSale.getSellers().contains(customer.getName()) && !loanForSale.getOwner().equals(customer.getName()))
                //CHECKMARK
                loansToBuy.add(createLoanDTO(loanForSale, 0, loanForSale.getBuyPrice())); //TODO::REMEMBER THAT CURRENTLY IT DOESN'T CHECK IF HE LENDS FOR THE LOAN ALREADY, AS INTENDED
        }
        return new CustomerDTO(customer.getName(), customer.getBalance(), transactionDTOList , ownerDTOList, lenderDTOList,
                notificationDTOList, loansToBuy, loansToSell, customer.getOtherNotifications());
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
        if(isRewind)
            return history.get(rewindYaz).getKey().getPossibleCategories();
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


    private void saveHistory() {
        List<CustomerDTO> historyCustomerList=new LinkedList<>();
        List<LoanDTO> historyLoanList=new LinkedList<>();
        Map<String, CustomerDTO> historyCustomerMap=new HashMap<>();
        Set<LoanDTO> loanToSellList=new HashSet<>();
        for(String name : customers.keySet()) {
            CustomerDTO customer=createCustomerDTO(name);
            historyCustomerMap.put(name, customer);
            historyCustomerList.add(customer);
        }
        for(String id : loans.keySet()) {
            LoanDTO loan=createLoanDTO(id);
            historyLoanList.add(loan);
            if(!loans.get(id).getSellers().isEmpty())
                loanToSellList.add(loan);
        }
        AdminDatabase data=new AdminDatabase(historyCustomerList, historyLoanList,loanToSellList,new LinkedList<>(possibleCategories),currentYaz, true);
        Pair<AdminDatabase, Map<String, CustomerDTO>> pair=new Pair<>(data, historyCustomerMap);
        history.put(currentYaz, pair);
    }

    public void advanceTime(){
        saveHistory();
        ++currentYaz;
        for(Customer customer :customers.values()) {
            for(String id: customer.getBorrowerLoans()) {
                Loan loan = loans.get(id);
                if (loan.getStatus().equals("active") || loan.getStatus().equals("risk")) {
                    loan.setYazCount(loan.getYazCount() + 1);
                    if (loan.isPayingPeriod() && loan.getStatus().equals("active")) { //if the loan missed the paying period
                        loan.setStatus("risk");
                        loansForSale.remove(loan);
                        for(String sellerName : loan.getSellers()) {
                            customers.get(sellerName).getOtherNotifications().add("The loan: " + loan.getId() + " became risk" + " at yaz: " + currentYaz);
                            customers.get(sellerName).getSelling().remove(id);
                        }
                        loan.getSellers().clear();
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
        double interestToPay=loan.totalInterest() - loan.getInterestPaid();
        outOfRisk(loan, amountToPay, interestToPay);
        finishLoan(loan);
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
            finishLoan(loan);
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

    public void finishLoan(Loan loan) {
        loan.setStatus("finished");
        for(String seller : loan.getSellers()){
            customers.get(seller).getOtherNotifications().add("The loan: " + loan.getId() + " became finished " + "at yaz: " + currentYaz);
            customers.get(seller).getSelling().remove(loan.getId());
        }
        loan.getSellers().clear();
        loansForSale.remove(loan);
        loan.setFinishYaz(currentYaz);

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
            if(loanAmount.equals(amountPaid)) {
                finishLoan(loan);
            }
            loan.setPayingPeriod(false);
        }
        else if(loan.getStatus().equals("risk")) {
            double entirePaymentAmount=loan.getTotalAmountToPayForPayment()/(1+loan.getInterestPerPayment()/100);
            double entirePaymentInterest=loan.getTotalAmountToPayForPayment() - entirePaymentAmount;
            outOfRisk(loan, entirePaymentAmount, entirePaymentInterest);
            if((int)loan.getAmount()==(int)loan.getAmountPaid()) { // casting for weird floating point cases
                finishLoan(loan);
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

    public void sellLoan(String loanID, String seller) {

        //add a check that the loan is active...
        //add a check that the seller does indeed lend to the loan.
        Loan loan=loans.get(loanID);
        loan.getSellers().add(seller);
        Customer customer=customers.get(seller);
        customer.getSelling().add(loanID);
        loansForSale.add(loan);
        customers.get(seller).getOtherNotifications().add("The loan: " + loanID + " is now up for sale at yaz: " + currentYaz);
    }
    public void buyLoan(String loanID, String buyer) throws NotEnoughMoneyException {
        //check that the loan is up for sale
        //check that buyer isn't a lender nor the owner
        Customer customer=customers.get(buyer);
        Loan loan=loans.get(loanID);
        //check enough money
        double amountLeftToPay=loan.getAmount() - loan.getAmountPaid();
        double amountToPay=0;

        for(String sellerID : loan.getSellers()){
            double sellFor=amountLeftToPay * loan.ownershipPercent(sellerID);
            amountToPay+=sellFor;
        }
        if(customer.getBalance() < amountToPay)
            throw new NotEnoughMoneyException(customer.getBalance(), amountToPay);
        for(String sellerID : loan.getSellers()) {
            double sellFor=amountLeftToPay * loan.ownershipPercent(sellerID);
            Customer seller=customers.get(sellerID);
            seller.getSelling().remove(loanID);
            seller.getLenderLoans().remove(loan.getId());
            charge(sellerID, sellFor);
            loan.getLenders().remove(sellerID);
            customers.get(sellerID).getOtherNotifications().add("The loan: " + loanID + " sold for " + sellFor + " at yaz: " + currentYaz);
        }
        double alreadyInvested=0;
        if(loan.getLenders().containsKey(buyer))
            alreadyInvested=loan.getLenders().get(buyer);
        else
            customer.getLenderLoans().add(loanID);
        loan.getLenders().put(buyer, amountToPay+alreadyInvested);
        withdraw(buyer, amountToPay);
        loan.getSellers().clear();
        customers.get(buyer).getOtherNotifications().add("The loan: " + loanID + " bought for " + amountToPay + " at yaz: " + currentYaz);
        loansForSale.remove(loan);
    }

    public void rewind(int yaz) {
        rewindYaz=yaz;
        if(rewindYaz==currentYaz)
            isRewind=false;
        else
            isRewind=true;
    }

    public boolean isRewind() {
        return isRewind;
    }
}



