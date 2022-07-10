package dto.database;

import dto.customerDTO.CustomerDTO;
import dto.loanDTO.LoanDTO;

import java.util.List;

public class CustomerDatabase {
    private final CustomerDTO customer;
    private final List<String> categories;
    private final int yaz;
    private final boolean isRewind;
    //filteredLoans


    public CustomerDatabase(CustomerDTO customer, List<String> categories, int yaz, boolean isRewind) {
        this.customer = customer;
        this.categories = categories;
        this.yaz = yaz;
        this.isRewind = isRewind;
    }

    public CustomerDTO getCustomer() {
        return customer;
    }

    public List<String> getCategories() {
        return categories;
    }

    public int getYaz() {
        return yaz;
    }

    public boolean isRewind() {
        return isRewind;
    }
}
