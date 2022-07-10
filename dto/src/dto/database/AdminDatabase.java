package dto.database;

import dto.customerDTO.CustomerDTO;
import dto.loanDTO.LoanDTO;

import java.util.List;
import java.util.Set;

public class AdminDatabase {
    private List<CustomerDTO> customerList;
    private List<LoanDTO> loanList;
    private Set<LoanDTO> loansForSale;
    private List<String> possibleCategories;
    private int yaz;
    private boolean isRewind;


    public AdminDatabase(List<CustomerDTO> customerList, List<LoanDTO> loanList, int yaz, boolean isRewind) {
        this.customerList = customerList;
        this.loanList = loanList;
        this.yaz = yaz;
        this.isRewind = isRewind;
    }

    public AdminDatabase(List<CustomerDTO> customerList, List<LoanDTO> loanList, Set<LoanDTO> loansForSale, List<String> possibleCategories, int yaz, boolean isRewind) {
        this.customerList = customerList;
        this.loanList = loanList;
        this.loansForSale = loansForSale;
        this.possibleCategories = possibleCategories;
        this.yaz = yaz;
        this.isRewind = isRewind;
    }

    public List<CustomerDTO> getCustomerList() {
        return customerList;
    }

    public List<LoanDTO> getLoanList() {
        return loanList;
    }

    public int getYaz() {
        return yaz;
    }

    public Set<LoanDTO> getLoansForSale() {
        return loansForSale;
    }

    public List<String> getPossibleCategories() {
        return possibleCategories;
    }

    public boolean isRewind() {
        return isRewind;
    }
}
