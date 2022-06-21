package ui.customer;

import dto.loanDTO.LoanDTO;
import engine.Engine;
import javafx.concurrent.Task;

import java.util.List;
import java.util.Set;

public class ScrambleTask extends Task<List<LoanDTO>> {
    private Engine engine;
    private String customerName;
    private Set<String> categories;
    private int interest;
    private int yaz;
    private int maximumOpenLoans;

    public ScrambleTask(Engine engine, String customerName, Set<String> categories, int interest, int yaz, int maximumOpenLoans) {
        this.engine = engine;
        this.customerName = customerName;
        this.categories = categories;
        this.interest = interest;
        this.yaz = yaz;
        this.maximumOpenLoans = maximumOpenLoans;
    }

    @Override
    protected List<LoanDTO> call() throws Exception {
        List<LoanDTO> loans=engine.filteredLoans(customerName, categories, interest, yaz, maximumOpenLoans);
        updateProgress(0.25, 1);
        Thread.sleep(500);
        updateProgress(0.5, 1);
        Thread.sleep(500);
        updateProgress(0.75, 1);
        Thread.sleep(500);
        updateProgress(1, 1);
        updateValue(loans);
        return loans;
    }
}
