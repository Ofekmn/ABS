package engine.exception.xml;



public class LoanFieldDoesNotExist extends Exception {
    private final String loanID;
    private final String fieldType;
    private final String name;

    public LoanFieldDoesNotExist(String loanID, String fieldType, String name) {
        this.loanID = loanID;
        this.fieldType = fieldType;
        this.name = name;
    }

    public String getLoanID() {return loanID; }

    public String getFieldType() { return fieldType;
    }

    public String getName() {
        return name;
    }

    public String getMessage() {return "The loan " + getLoanID() + " contains the " + getFieldType() + " " + getName() +
                ", but the " + getFieldType() + " " + getName() + " doesn't exist.";}
}
