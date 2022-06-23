package engine;

import dto.customerDTO.CustomerDTO;
import dto.loanDTO.LoanDTO;
import engine.exception.xml.LoanFieldDoesNotExist;
import engine.exception.xml.NameException;
import engine.exception.xml.YazException;
import javafx.util.Pair;

import java.io.*;
import java.util.List;
import java.util.Set;

public interface Engine  {


    void loadFile(InputStream file, String name) throws NameException, LoanFieldDoesNotExist, YazException;
    void loadFileOld(InputStream xmlFile) throws NameException, LoanFieldDoesNotExist, YazException;
    boolean isFileLoaded();
    List<LoanDTO> getAllLoans();
    List<CustomerDTO> getAllCustomersDetails();
    List<Pair<String, Double>> getCustomers();
    void charge(String name, double amount);
    void withdraw(String name, double amount);
    List<LoanDTO>  filteredLoans(String name, Set<String> categories, double interest, int totalYaz, int maximumOpenLoans);
    double assign(List<String> loanID, int amount, String customerName, int maximumLoanOwnership);
    void advanceTime();
    List<String> getCategories();
    int getCurrentYaz();
    CustomerDTO createCustomerDTO(String name);
    void closeLoan(String loanID);
    LoanDTO createLoanDTO(String Id);
    void payLoan(String loanID);
    void payLoanRisk(String loanID, double totalSum);
}
