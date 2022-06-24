package dto;

import java.util.Set;

public class FilterDTO {
    int amount;
    Set<String> categories;
    int interest;
    int yaz;
    int maximumOpenLoans;

    public FilterDTO(int amount, Set<String> categories, int interest, int yaz, int maximumOpenLoans) {
        this.amount = amount;
        this.categories = categories;
        this.interest = interest;
        this.yaz = yaz;
        this.maximumOpenLoans = maximumOpenLoans;
    }

    public int getAmount() {
        return amount;
    }

    public Set<String> getCategories() {
        return categories;
    }

    public int getInterest() {
        return interest;
    }

    public int getYaz() {
        return yaz;
    }

    public int getMaximumOpenLoans() {
        return maximumOpenLoans;
    }
}
