package dto.customerDTO;

import dto.loanDTO.LoanDTO;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CustomerDTO {
    private final String name;
    private double currentAmount;
    private List<TransactionDTO> transactionDTOList;
    private List<LoanDTO> ownerDTO;
    private List<LoanDTO> lenderDTO;
    private List<NotificationDTO> notificationDTOList;
    private Map<String, Integer> ownerStatusCount;
    private Map<String, Integer> lenderStatusCount;
    private String currentAmountString;
    private List<LoanDTO> loansToBuy;
    private List<LoanDTO> loansToSell;
    private List<String> otherNotifications;
//    private int ownerNewCount;
//    private int ownerPendingCount;
//    private int ownerActiveCount;
//    private int ownerRiskCount;
//    private int ownerFinishedCount;
//    private int lenderNewCount;
//    private int lenderPendingCount;
//    private int lenderActiveCount;
//    private int lenderRiskCount;
//    private int lenderFinishedCount;


    public CustomerDTO(String name, double currentAmount, List<TransactionDTO> transactionDTOList ,List<LoanDTO> ownerDTOList, List<LoanDTO> lenderDTOList,
                       List<NotificationDTO> notificationDTOList, List<LoanDTO> loansToBuy, List<LoanDTO> loansToSell, List<String> otherNotifications) {
        this.name = name;
        this.currentAmount = currentAmount;
        this.transactionDTOList=transactionDTOList;
        this.ownerDTO = ownerDTOList;
        this.lenderDTO = lenderDTOList;
        this.notificationDTOList=notificationDTOList;
        ownerStatusCount=calculateStatusCount(ownerDTOList);
        lenderStatusCount=calculateStatusCount(lenderDTOList);
        currentAmountString=String.format("%.1f", currentAmount);
        this.loansToBuy = loansToBuy;
        this.loansToSell=loansToSell;
        this.otherNotifications=otherNotifications;
    }

    public CustomerDTO(String name) {
        this.name = name;
        transactionDTOList=new LinkedList<>();
        ownerDTO=new LinkedList<>();
        lenderDTO=new LinkedList<>();
        notificationDTOList=new LinkedList<>();
        ownerStatusCount=new HashMap<>();
        lenderStatusCount=new HashMap<>();
        currentAmountString="";
        loansToBuy=new LinkedList<>();
        loansToSell=new LinkedList<>();
        otherNotifications= new LinkedList<>();
    }

    public String getName() {
        return name;
    }

    public double getCurrentAmount() {
        return currentAmount;
    }

    public List<LoanDTO> getOwnerDTO() {
        return ownerDTO;
    }

    public Map<String, Integer> getOwnerStatusCount() {
        return ownerStatusCount;
    }

    public Map<String, Integer> getLenderStatusCount() {
        return lenderStatusCount;
    }

    public List<LoanDTO> getLenderDTO() {
        return lenderDTO;
    }

    public List<TransactionDTO> getTransactionDTOList() {
        return transactionDTOList;
    }

    public List<NotificationDTO> getNotificationDTOList() {
        return notificationDTOList;
    }

    private Map<String, Integer> calculateStatusCount(List<LoanDTO> loans) {
        if(loans.isEmpty())
            return null;
        Map<String, Integer> statusCount= new HashMap<>();
        for (LoanDTO loan : loans) {
            if(statusCount.containsKey(loan.getStatus())) {
                Integer integer=statusCount.get(loan.getStatus());
                ++integer;
                statusCount.put(loan.getStatus(), integer);

            }
            else{
                statusCount.put(loan.getStatus(), 1);
            }
        }
        return statusCount;
    }

    public List<LoanDTO> getLoansToBuy() {
        return loansToBuy;
    }

    public List<LoanDTO> getLoansToSell() {
        return loansToSell;
    }

    public List<String> getOtherNotifications() {
        return otherNotifications;
    }
}
