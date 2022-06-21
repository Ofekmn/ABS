package dto.customerDTO;

import dto.loanDTO.LoanDTO;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerDTO {
    private final String name;
    private final double currentAmount;
    private final List<TransactionDTO> transactionDTOList;
    private final List<LoanDTO> ownerDTO;
    private final List<LoanDTO> lenderDTO;
    private final List<NotificationDTO> notificationDTOList;
    private final Map<String, Integer> ownerStatusCount;
    private final Map<String, Integer> lenderStatusCount;
    private final String currentAmountString;
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
                       List<NotificationDTO> notificationDTOList) {
        this.name = name;
        this.currentAmount = currentAmount;
        this.transactionDTOList=transactionDTOList;
        this.ownerDTO = ownerDTOList;
        this.lenderDTO = lenderDTOList;
        this.notificationDTOList=notificationDTOList;
        this.
        ownerStatusCount=calculateStatusCount(ownerDTOList);
        lenderStatusCount=calculateStatusCount(lenderDTOList);
        currentAmountString=String.format("%.1f", currentAmount);

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


}
