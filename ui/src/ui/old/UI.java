//package ui;
//
//import dto.customerDTO.CustomerDTO;
//import dto.loanDTO.LoanDTO;
//import engine.Engine;
//import engine.EngineImpl;
//import engine.exception.xml.FileExtensionException;
//import engine.exception.xml.LoanFieldDoesNotExist;
//import engine.exception.xml.NameException;
//import engine.exception.xml.YazException;
//import javafx.util.Pair;
//
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.util.*;
//
//public class UI {
//
//    public UI(){}
//
//    public void menu() {
//        Scanner scanner = new Scanner(System.in);
//        Engine engine=new EngineImpl();
//        boolean end=false;
//        int userIntegerInput;
//        while(!end) {
//            System.out.println("Current Yaz: " + engine.getCurrentYaz());
//            System.out.println("Please enter one of the following numbers: ");
//            System.out.println("1) Load a file");
//            System.out.println("2) Show all loans information");
//            System.out.println("3) Show all customers information");
//            System.out.println("4) Deposit money to a chosen customer");
//            System.out.println("5) Withdraw money from a chosen customer");
//            System.out.println("6) Assign loans to a chosen customer");
//            System.out.println("7) Advance time by 1 yaz and proceed payments");
//            System.out.println("8) Exit");
//            System.out.println("9) Save");
//            try {
//                userIntegerInput=scanner.nextInt();
//                switch(userIntegerInput){
//                    case 1:
//                        int fileChoice=0;
//                        boolean validChoice=false;
//
//                        do {
//                            System.out.println("Please enter one of the following numbers: ");
//                            System.out.println("1) load an xml file");
//                            System.out.println("2) load a dat file that was generated previously by command 9");
//                            try{
//                                fileChoice=scanner.nextInt();
//                                if(fileChoice==1 || fileChoice==2)
//                                    validChoice=true;
//                                else
//                                    System.out.println("Invalid input: " + fileChoice + " is outside the valid range(1 or 2)");
//                            }
//                            catch(InputMismatchException e){
//                                System.out.println("Invalid input: " + scanner.nextLine() + " is not an integer.");
//                            }
//                        } while(!validChoice);
//                        String path;
//                        scanner.nextLine();
//                        boolean invalidPath=true;
//                        do {
//                        if(fileChoice==1)
//                            System.out.println("Please enter the full path name to your xml file, including the .xml file extension: ");
//                        else
//                            System.out.println("Please enter the full path name to your dat file, including the .dat file extension: ");
//                        path=scanner.nextLine();
//                        if(fileChoice==1) {
//                            try {
//                                engine.loadFile(path);
//                                invalidPath=false;
//                                System.out.println("File loaded successfully.");
//                            } catch (FileExtensionException e) {
//                                System.out.println("Error: Wrong file extension. The file should end with a .xml extension.");
//                                System.out.println("Example: C:\\Some Folder\\File.xml");
//                            } catch (FileNotFoundException e) {
//                                System.out.println("Error: the file  wasn't found. Please make sure the path leads to an existing xml file");
//                                System.out.println("Example: C:\\Some Folder\\File.xml");
//                            } catch (NameException e) {
//                                System.out.println("Invalid File: there are 2 different instances of the " + e.getType() + " " + e.getName());
//                            } catch (LoanFieldDoesNotExist e) {
//                                System.out.println("Invalid File: the loan " + e.getLoanID() + " contains the " + e.getFieldType() + " " + e.getName() +
//                                        ", but the " + e.getFieldType() + " " + e.getName() + " doesn't exist.");
//                            } catch (YazException e) {
//                                System.out.println("Invalid File: for the loan: " + e.getLoanID() + " the total loan time (" + e.getTotalYaz() +
//                                        ") isn't divided by the payment rate (" + e.getPaymentRate() + ")");
//                            }
//                        }
//                        else {
//                            try {
//                                engine.loadSystem(path);
//                                invalidPath=false;
//                                System.out.println("File loaded successfully");
//                            }
//
//                            catch (FileExtensionException e) {
//                                System.out.println("Error: Wrong file extension. The file should end with a .dat extension.");
//                            }
//
//                            catch (IOException e){
//                                System.out.println("Error: the file  wasn't found. Please make sure the path leads to an existing dat file");
//                            }
//                            catch (ClassNotFoundException ignored){
//
//                            }
//                        }
//                        } while (invalidPath);
//                        break;
//                    case 2:
//                        if(engine.isFileLoaded()){
//                            System.out.println("Error: File isn't loaded yet. Please load a file by command number 1 before doing anything else.");
//                            break;
//                        }
//                        for(LoanDTO loan: engine.getAllLoans()) {
//                            System.out.println("*********************************************************************");
//                            System.out.println("ID: " + loan.getId() + System.lineSeparator() +  "Owner: " + loan.getOwner() + System.lineSeparator() + "Category: " +
//                                    loan.getCategory() + System.lineSeparator() + "Amount: "  + String.format("%.1f",loan.getAmount()) + "$" + System.lineSeparator() +  "Total loan time: " + loan.getTotalYaz() + " yaz" + System.lineSeparator() +
//                                    "Pays every " + loan.getPaysEveryYaz() + " yaz" + System.lineSeparator() +   "Interest per payment: "+ String.format("%.1f",loan.getInterestPerPayment())
//                                    + "%" + System.lineSeparator() + "Status: " + loan.getStatus());
//                            if(!loan.getStatus().equals("new")) {
//                                System.out.println("The following customers are the lenders for this loan:");
//                                for (String name : loan.getLenders().keySet() ) {
//                                    System.out.println("Name: " + name + " Amount raised : " + String.format("%.1f",loan.getLenders().get(name))  +"$");
//                                }
//                                if(loan.getStatus().equals("pending"))
//                                    System.out.println(String.format("%.1f",loan.getRaisedMoney()) + "$ was already raised for this loan. For the loan to become active, it needs more " + String.format("%.1f",loan.getMoneyLeftToBecomeActive()) + "$");
//                                else {
//                                    if(loan.getStatus().equals("finished"))
//                                        System.out.println("This loan became active at: "+ loan.getStartingYaz()+ " yaz and finished at: " + loan.getFinishYaz() + " yaz");
//                                    else
//                                        System.out.println("This loan became active at: "+ loan.getStartingYaz() + " yaz.  The next payment is at: " + loan.getNextPaymentYaz() + " yaz");
//                                    System.out.println("The following payments happened: ");
//                                    for(int i=0; i<loan.getPaymentYaz().size();++i){
//                                        System.out.println("Yaz: " + loan.getPaymentYaz().get(i) + " Amount: " + String.format("%.1f",loan.getPaymentAmount().get(i)) +
//                                                "$  Interest: " + String.format("%.1f",loan.getPaymentInterest().get(i)) + "$ Total payment: " + String.format("%.1f",loan.getPaymentAmount().get(i) + loan.getPaymentInterest().get(i)) + "$");
//                                    }
//                                    if(!loan.getStatus().equals("finished")){
//                                        System.out.println("Total amount paid: " + String.format("%.1f",loan.getPaidAmount()) + "$  Total interest paid: " + String.format("%.1f",loan.getPaidInterest()) + "$");
//                                        System.out.println("Total amount left: " + String.format("%.1f",loan.getAmountLeftToPay()) + "$  Total interest left: " + String.format("%.1f",loan.getInterestLeftToPay()) + "$");
//                                    }
//                                    if(loan.getStatus().equals("risk")){
//                                        System.out.println("* There are" + loan.getDelayedPayments().getKey() + "delayed payments. The total amount of the delayed payments is: " +
//                                                String.format("%.1f",loan.getDelayedPayments().getValue()) + "$ *");
//
//                                    }
//                                }
//                            }
//                            System.out.println("*********************************************************************");
//                        }
//                        break;
//                    case 3:
//                        if(engine.isFileLoaded()){
//                            System.out.println("Error: File isn't loaded yet. Please load a file by command number 1 before doing anything else.");
//                            break;
//                        }
//
//                        List<CustomerDTO> customersDetails=engine.getAllCustomersDetails();
//                        for(CustomerDTO customer: customersDetails) {
//                            System.out.println("*********************************************************************");
//                            System.out.println("Name: " + customer.getName() + " Balance: " + String.format("%.1f",customer.getCurrentAmount()) + "$");
//                            System.out.println("Transactions: ");
//                            for (int i = customer.getTransactionYaz().size()-1; i>=0 ; --i) {
//                                System.out.println("Yaz: " + customer.getTransactionYaz().get(i) + " Amount: " + customer.getTransactionsSign().get(i)
//                                        + String.format("%.1f",customer.getTransactionsMoney().get(i))+ " Balance before the transaction: " + String.format("%.1f",customer.getTransactionsBeforeTransaction().get(i))
//                                        + " Balance after the transaction: " + String.format("%.1f",customer.getTransactionsAfterTransaction().get(i)));
//                            }
//                            if (customer.getOwnerDTO().isEmpty())
//                                System.out.println("This customer doesn't own any loan.");
//                            else
//                                System.out.println("This customer is the owner of the following loans: ");
//                                for(LoanDTO loan: customer.getOwnerDTO()){
//                                    System.out.println("---------------------------------------------------------------------");
//                                    printLoanForCustomer(loan);
//                                    System.out.println("---------------------------------------------------------------------");
//                                }
//                            if (customer.getLenderDTO().isEmpty())
//                                System.out.println("This customer doesn't lend to any loan.");
//                            else
//                                System.out.println("This customer lends to the following loans: ");
//                            for(LoanDTO loan: customer.getLenderDTO()){
//                                System.out.println("---------------------------------------------------------------------");
//                                printLoanForCustomer(loan);
//                                System.out.println("---------------------------------------------------------------------");
//                            }
//                            System.out.println("*********************************************************************");
//                        }
//                        break;
//                    case 4:
//                        if(engine.isFileLoaded()){
//                            System.out.println("Error: File isn't loaded yet. Please load a file by command number 1 before doing anything else.");
//                            break;
//                        }
//
//                        List<Pair<String, Double>> CustomersToDeposit=engine.getCustomers();
//                        boolean wrongInput=true;
//                        int customerToDepositFromNumber = 0;
//                        do {
//                            System.out.println("Please choose one of the following integers that represents the customer you wish to deposit to: ");
//                            for(int i=0;i < CustomersToDeposit.size();++i){
//                                System.out.println(i+1+") Name: "+CustomersToDeposit.get(i).getKey()+ " Balance: " + (int) Math.floor(CustomersToDeposit.get(i).getValue()) + "$");
//                            }
//                            try{
//                                customerToDepositFromNumber=scanner.nextInt();
//                                if(customerToDepositFromNumber>0 && customerToDepositFromNumber <= CustomersToDeposit.size())
//                                    wrongInput=false;
//                                else
//                                    System.out.println("Invalid input: the number " + customerToDepositFromNumber + " is outside the valid range(an integer from 1 to "+CustomersToDeposit.size()+")");
//                            }
//                            catch(InputMismatchException exception){
//                                System.out.println("Invalid input: " + scanner.nextLine() + " is not an integer.");
//                            }
//                        } while(wrongInput);
//                        wrongInput=true;
//                        int amountToDeposit = 0;
//
//                        do{
//                            System.out.println("Please choose the amount you wish to deposit (a non-negative integer) ");
//                            try{
//                                amountToDeposit= scanner.nextInt();
//                                if(amountToDeposit >= 0 )
//                                    wrongInput=false;
//                                else
//                                    System.out.println("Invalid input: " + amountToDeposit + " is a negative number");
//                            }
//                            catch(InputMismatchException exception){
//                                System.out.println("Invalid input: " + scanner.nextLine() + " is not an integer.");
//                            }
//                            if(amountToDeposit!=0) {
//                                engine.deposit(CustomersToDeposit.get(customerToDepositFromNumber-1).getKey(),amountToDeposit);
//                                System.out.println("Successfully deposited " + amountToDeposit +"$ into " + CustomersToDeposit.get(customerToDepositFromNumber-1).getKey()
//                                        + "'s account.");
//                            }
//                            else
//                                System.out.println("There is no point in depositing 0$. Going back to the main menu.");
//                        } while(wrongInput);
//                        break;
//                    case 5:
//                        if(engine.isFileLoaded()){
//                            System.out.println("Error: File isn't loaded yet. Please load a file by command number 1 before doing anything else.");
//                            break;
//                        }
//                        List<Pair<String, Double>> customersToWithdraw=engine.getCustomers();
//                        boolean invalidInput=true;
//                        int customerToWithdrawFromNumber = 0;
//                        do {
//                            System.out.println("Please choose one of the following integers that represents the customer you wish to withdraw from: ");
//                            for(int i=0;i < customersToWithdraw.size();++i){
//                                System.out.println(i+1+") Name: "+ customersToWithdraw.get(i).getKey() + " Balance: " + (int) Math.floor(customersToWithdraw.get(i).getValue()) + "$");
//                            }
//                            try{
//                                customerToWithdrawFromNumber=scanner.nextInt();
//                                if(customerToWithdrawFromNumber>0 && customerToWithdrawFromNumber <= customersToWithdraw.size())
//                                    invalidInput=false;
//                                else
//                                    System.out.println("Invalid input: " + customerToWithdrawFromNumber + " is outside the valid range(an integer from 1 to "+ customersToWithdraw.size()+")");
//                            }
//                            catch(InputMismatchException exception){
//                                System.out.println("Invalid input: " + scanner.nextLine() + " is not an integer");
//                            }
//                        } while(invalidInput);
//                        invalidInput=true;
//                        int amountToWithdraw = 0;
//                        do{
//                            System.out.println("Please choose the amount you wish to withdraw(a positive integer)");
//                            try{
//                                amountToWithdraw= scanner.nextInt();
//                                if(amountToWithdraw >= 0 && amountToWithdraw <= customersToWithdraw.get(customerToWithdrawFromNumber-1).getValue()){
//                                    invalidInput=false;
//                                }
//
//                                else
//                                    System.out.println("Invalid input: " + amountToWithdraw + " is outside the valid range(an integer between 0 and " + (int) Math.floor(customersToWithdraw.get(customerToWithdrawFromNumber-1).getValue())+ ")");
//                            }
//                            catch(InputMismatchException exception){
//                                System.out.println("Invalid input: " + scanner.nextLine() + " is not an integer");
//                            }
//                        } while(invalidInput);
//                        if(amountToWithdraw!=0){
//                            engine.withdraw(customersToWithdraw.get(customerToWithdrawFromNumber-1).getKey(), amountToWithdraw);
//                            System.out.println("Successfully withdrawn " + amountToWithdraw + "$ from " + customersToWithdraw.get(customerToWithdrawFromNumber-1).getKey() + "'s account");
//                        }
//                        else
//                            System.out.println("There is no point in withdrawing 0$. Going back to the main menu.");
//                        break;
//                    case 6:
//                        if(engine.isFileLoaded()){
//                            System.out.println("Error: File isn't loaded yet. Please load a file by command number 1 before doing anything else.");
//                            break;
//                        }
//                        int customerToAssign=0;
//                        List<Pair<String, Double>> customers=engine.getCustomers();
//                        boolean validAssign=false;
//                        do {
//                            System.out.println("Please choose a customer you want to assign the loans to, by entering the number that represents the customer:" );
//                            for (int i = 0; i < customers.size(); i++) {
//                                System.out.println(i+1 + ") Name: " + customers.get(i).getKey() + " Balance: " + (int) Math.floor(customers.get(i).getValue()) + "$");
//                            }
//                            try {
//                                customerToAssign = scanner.nextInt();
//                                if (customerToAssign <= 0 || customerToAssign > customers.size())
//                                    System.out.println("Invalid input: " + customerToAssign + " is outside the valid range (an integer from 1 to " + customers.size()+ ")");
//                                else
//                                    validAssign=true;
//
//                            } catch (InputMismatchException e) {
//                                System.out.println("Invalid input: " + scanner.nextLine() + " is not an integer");
//                            }
//                        } while(!validAssign);
//                        validAssign=false;
//                        System.out.println("Please enter the amount of money you wish to invest");
//                        int amountToAssign = 0;
//                        do {
//                            try {
//                                amountToAssign = scanner.nextInt();
//                                if (amountToAssign < 0 || amountToAssign > customers.get(customerToAssign-1).getValue())
//                                    System.out.println("Invalid input: " + amountToAssign + " is outside the valid range(an integer from 0 to " + customers.get(customerToAssign-1).getValue().intValue() + ")");
//                                else
//                                    validAssign=true;
//
//                            } catch (InputMismatchException e) {
//                                System.out.println("Invalid input: " + scanner.nextLine() + " is not an integer");
//                            }
//                        } while(!validAssign);
//                        if(amountToAssign==0){
//                            System.out.println("There is no point in investing 0$. Going back to the main menu. ");
//                            break;
//                        }
//
//                        scanner.nextLine();
//
//                        Set<String> categorySet=new HashSet<>();
//                        do{
//                            System.out.println("Please enter the categories you want to invest in by entering corresponding integers seperated by space. ");
//                            System.out.println("If you don't care about the category, simply press enter: ");
//                            List<String> categories= engine.getCategories();
//                            for (int i = 0; i < categories.size(); i++) {
//                                System.out.println(i+1+ ") " + categories.get(i));
//                            }
//                            String input=scanner.nextLine();
//                            if(input.equals(""))
//                                break;
//                            boolean validCategory=true;
//                            for (int i = 0; i < input.split(" ").length; i++) {
//                                try{
//                                    int number=Integer.parseInt(input.split(" ")[i]);
//                                    if(number<=0 || number> categories.size()) {
//                                        System.out.println("Invalid input for the " + getOrdinal(i+1)+ " number: "  + number + " is outside the valid range(an integer from 1 to " + categories.size()+")");
//                                        validCategory=false;
//                                    }
//                                    else {
//                                        categorySet.add(categories.get(number-1));
//                                    }
//
//                                }
//                                catch (NumberFormatException e){
//                                    System.out.println("Invalid input for the " + getOrdinal(i+1)+ " entry: "+ input.split(" ")[i] +" is not an integer");
//                                    scanner.nextLine();
//                                    validCategory=false;
//                                }
//                                if(!validCategory){
//                                    validAssign=false;
//                                    break;
//                                }
//                                else
//                                    validAssign=true;
//                            }
//                            if(!validAssign){
//                                System.out.print("An example for  a correct input:  \"1 2\" , without the quotation marks, would get the first 2 categories. ");
//                               // scanner.nextLine();
//                                categorySet.clear();
//                            }
//
//                        } while(!validAssign);
//                        int interest = 0;
//                        validAssign=false;
//                        do {
//                            System.out.println("Please enter the minimum interest you want for the loan");
//                            System.out.println("If you don't care about the minimum, simply press 0. ");
//                            try{
//                                interest=scanner.nextInt();
//                                if(interest< 0 )
//                                    System.out.println("Invalid input: " + interest + "is a negative number. " );
//                                else
//                                    validAssign=true;
//                            }
//                            catch(InputMismatchException e){
//                                System.out.println("Invalid input: " + scanner.nextLine() + " is not an integer.");
//                            }
//
//
//                        }while(!validAssign);
//
//                        int minYaz=0;
//                        validAssign=false;
//                        do {
//                            System.out.println("Please enter the minimum yaz you want for the loan to take");
//                            System.out.println("If you don't care about the minimum, simply press 0. ");
//                            try{
//                                minYaz=scanner.nextInt();
//                                if(minYaz< 0 )
//                                    System.out.println("Invalid input: " + minYaz + "is a negative number. " );
//                                else
//                                    validAssign=true;
//                            }
//                            catch(InputMismatchException e){
//                                System.out.println("Invalid input: " + scanner.nextLine() + " is not an integer.");
//                            }
//
//
//                        }while(!validAssign);
//                        List<LoanDTO> filteredLoans=engine.filteredLoans(customers.get(customerToAssign-1).getKey(), categorySet, interest,minYaz);
//                        if(filteredLoans.isEmpty()) {
//                            System.out.println("There are no loans that match your criteria");
//                            break;
//                        }
//
//
//                        Set<String> loanIDs=new HashSet<>();
//
//                        do{
//                            System.out.println("Please enter the loans you want to invest in by entering corresponding integers seperated by space ");
//                            int j=1;
//                            for (LoanDTO loanDTO: filteredLoans) {
//                                System.out.println();
//                                System.out.println("---------------------------------------------------------------------");
//                                System.out.print(j+ ") ");
//                                printLoanForCustomer(loanDTO);
//                                System.out.println("---------------------------------------------------------------------");
//                                ++j;
//                            }
//                            Scanner in=new Scanner(System.in);
//                            String input=in.nextLine();
//                            boolean validLoan=true;
//                            for (int i = 0; i < input.split(" ").length; i++) {
//                                try{
//                                    int number=Integer.parseInt(input.split(" ")[i]);
//                                    if(number<=0 || number> filteredLoans.size()) {
//                                        System.out.println("Invalid input for the " + getOrdinal(i+1)+ " number: "  + number + " is outside the valid range(an integer from 1 to " + filteredLoans.size()+")");
//                                        validLoan=false;
//                                    }
//                                    else {
//                                        loanIDs.add(filteredLoans.get(number-1).getId());
//                                    }
//
//                                }
//                                catch (NumberFormatException e){
//                                    System.out.println("Invalid input for the " + getOrdinal(i+1)+ " entry: "+ input.split(" ")[i] +" is not an integer");
//                                    validLoan=false;
//                                }
//                                if(!validLoan){
//                                    validAssign=false;
//                                    break;
//                                }
//                                else
//                                    validAssign=true;
//                            }
//                            if(!validAssign){
//                                System.out.print("An example for a correct input:  \"1 2\" , without the quotation marks, would get you the first 2 loans. ");
//                                // scanner.nextLine();
//                                loanIDs.clear();
//                            }
//                        }  while(!validAssign);
//                        double invested=engine.assign(new ArrayList<>(loanIDs), amountToAssign,customers.get(customerToAssign-1).getKey());
//                        if(invested< amountToAssign)
//                            System.out.println("The total amount all the loans required was lower than " + amountToAssign + " and as such, only " + String.format("%.1f",invested) + "$ was invested");
//                        else
//                            System.out.println("Successfully managed to invest " + amountToAssign + "$");
//                        break;
//                    case 7:
//                        if(engine.isFileLoaded()){
//                            System.out.println("Error: File isn't loaded yet. Please load a file by command number 1 before doing anything else.");
//                            break;
//                        }
//                        engine.advanceTime();
//                        System.out.println("Successfully advanced time and increased yaz by 1. Previous yaz was " + (engine.getCurrentYaz() -1) + "the current yaz is: " + engine.getCurrentYaz());
//
//                        break;
//                    case 8:
//                        System.out.println("Goodbye.");
//                        scanner.close();
//                        end=true;
//                        break;
//                    case 9:
//                        String save;
//                        if(engine.isFileLoaded()){
//                            System.out.println("Error: File isn't loaded yet. Please load a file by command number 1 before doing anything else.");
//                            break;
//                        }
//                        System.out.println("Please enter the full path name for you want the file to have: ");
//                        System.out.println("Note that you don't have to enter the extension.");
//                        if(scanner.hasNext())
//                            scanner.nextLine();
//                        save=scanner.nextLine();
//                        try {
//                            engine.saveSystem(save);
//                            System.out.println("File successfully saved.");
//                        }
//                        catch (IOException e) {
//                            System.out.println("Failed to find directory");
//                    }
//                        break;
//                    default:
//                        System.out.println("Invalid input: " + userIntegerInput + " is outside the valid range(an integer from 1 to 8)");
//                        break;
//
//                }
//            }
//            catch(InputMismatchException exception) {
//                System.out.println("Invalid input: " + scanner.nextLine() + " is not an integer.");
//            }
//        }
//
//    }
//
//    public static void printLoanForCustomer(LoanDTO loan) {
//
//        System.out.println("Loan ID: " + loan.getId() + System.lineSeparator() + "Category: " + loan.getCategory() + System.lineSeparator() +
//                "Amount: " + String.format("%.1f",loan.getAmount()) + "$" + System.lineSeparator() +  "Pays every: " +
//                loan.getPaysEveryYaz() + " yaz" + System.lineSeparator() +  "Interest per payment: " + String.format("%.1f",loan.getInterestPerPayment()) + "%" + System.lineSeparator()
//                + "Total amount with interest : " + loan.getTotalMoney() + "$" + System.lineSeparator()+  "Status: " + loan.getStatus());
//        if (loan.getStatus().equals("pending"))
//            System.out.println("amount left to become active: " + String.format("%.1f",loan.getMoneyLeftToBecomeActive())+ "$");
//        if (loan.getStatus().equals("active"))
//            System.out.println("Next payment at: " + loan.getNextPaymentYaz() + " yaz. Expected amount: " + String.format("%.1f",loan.getExpectedAmount()) + "$");
//        if (loan.getStatus().equals("risk")) {
//            System.out.println("*There are" + loan.getDelayedPayments().getKey() + " delayed payments. Their total amount is: " + String.format("%.1f",loan.getDelayedPayments().getValue()) + "$ *");
//
//        }
//        if (loan.getStatus().equals("finished"))
//            System.out.println("This loan started at: " + loan.getStartingYaz() + " yaz and finished at " + loan.getFinishYaz() + " yaz");
//
//
//    }
//
//    public static String getOrdinal(int number) {
//        String[] suffixes = new String[]{"th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th"};
//        switch (number % 100) {
//            case 11:
//            case 12:
//            case 13:
//                return number + "th";
//            default:
//                return number + suffixes[number % 10];
//        }
//    }
//
//
//}


