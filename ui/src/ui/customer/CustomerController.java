package ui.customer;

import dto.customerDTO.CustomerDTO;
import dto.customerDTO.NotificationDTO;
import dto.customerDTO.TransactionDTO;
import dto.loanDTO.LoanDTO;
import engine.Engine;
import engine.exception.xml.LoanFieldDoesNotExist;
import engine.exception.xml.NameException;
import engine.exception.xml.YazException;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import okhttp3.*;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.Notifications;
import org.jetbrains.annotations.NotNull;
import ui.MainController;
import ui.util.HttpClientUtil;


import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static ui.util.Constants.UPLOAD_FILE_PAGE;
import static ui.util.Constants.GSON_INSTANCE;


public class CustomerController  {

    private final static int INVALID=-1;
    @FXML Label balanceLabel;
    @FXML TextField chargeTextField;
    @FXML TextField withdrawTextField;

    //loaner Table
    @FXML TableView<LoanDTO> loanerTable;
    @FXML TableColumn <LoanDTO, String> loanerIdColumn;
    @FXML TableColumn<LoanDTO, String> loanerOwnerColumn;
    @FXML TableColumn <LoanDTO, Double> loanerAmountColumn;
    @FXML TableColumn <LoanDTO, String> loanerCategoryColumn;
    @FXML TableColumn <LoanDTO, Double> loanerInterestColumn;
    @FXML TableColumn <LoanDTO, Integer> loanerTotalYazColumn;
    @FXML TableColumn <LoanDTO, Integer> loanerPaymentRateColumn;
    @FXML TableColumn <LoanDTO, String> loanerStatusColumn;
    @FXML TableColumn<LoanDTO, List<String>> loanerLenderListName;
    @FXML TableColumn<LoanDTO, List<Double>> loanerLenderListAmount;
    @FXML TableColumn<LoanDTO, String> loanerAmountRaisedColumn;
    @FXML TableColumn<LoanDTO, String> loanerAmountLeftToActive;
    @FXML TableColumn<LoanDTO, Integer> loanerStartingYazColumn;
    @FXML TableColumn<LoanDTO, Integer> loanerNextPaymentYazColumn;
    @FXML TableColumn<LoanDTO, List<Integer>> loanerPaymentYazColumn;
    @FXML TableColumn<LoanDTO, List<Double>> loanerPaymentAmountColumn;
    @FXML TableColumn<LoanDTO, List<Double>> loanerPaymentInterestColumn;
    @FXML TableColumn<LoanDTO, List<Double>> loanerPaymentTotalColumn;
    @FXML TableColumn<LoanDTO, String> loanerTotalPaidAmount;
    @FXML TableColumn<LoanDTO, String> loanerTotalPaidInterest;
    @FXML TableColumn<LoanDTO, String> loanerTotalAmountLeftToPay;
    @FXML TableColumn<LoanDTO, String> loanerTotalInterestLeftToPay;
    @FXML TableColumn<LoanDTO, Integer> loanerDelayedPaymentsNumber;
    @FXML TableColumn<LoanDTO, String> loanerDelayedPaymentsSum;
    @FXML TableColumn<LoanDTO, Integer> loanerFinishedYaz;

    //lender Table
    @FXML TableView<LoanDTO> lenderTable;
    @FXML TableColumn <LoanDTO, String> lenderIdColumn;
    @FXML TableColumn<LoanDTO, String> lenderOwnerColumn;
    @FXML TableColumn <LoanDTO, Double> lenderAmountColumn;
    @FXML TableColumn <LoanDTO, String> lenderCategoryColumn;
    @FXML TableColumn <LoanDTO, Double> lenderInterestColumn;
    @FXML TableColumn <LoanDTO, Integer> lenderTotalYazColumn;
    @FXML TableColumn <LoanDTO, Integer> lenderPaymentRateColumn;
    @FXML TableColumn <LoanDTO, String> lenderStatusColumn;
    @FXML TableColumn <LoanDTO, List<String>> lenderLenderListName;
    @FXML TableColumn <LoanDTO, List<Double>> lenderLenderListAmount;
    @FXML TableColumn<LoanDTO, String> lenderAmountRaisedColumn;
    @FXML TableColumn<LoanDTO, String> lenderAmountLeftToActive;
    @FXML TableColumn<LoanDTO, Integer> lenderStartingYazColumn;
    @FXML TableColumn<LoanDTO, Integer> lenderNextPaymentYazColumn;
    @FXML TableColumn<LoanDTO, List<Integer>> lenderPaymentYazColumn;
    @FXML TableColumn<LoanDTO, List<Double>> lenderPaymentAmountColumn;
    @FXML TableColumn<LoanDTO, List<Double>> lenderPaymentInterestColumn;
    @FXML TableColumn<LoanDTO, List<Double>> lenderPaymentTotalColumn;
    @FXML TableColumn<LoanDTO, String> lenderTotalPaidAmount;
    @FXML TableColumn<LoanDTO, String> lenderTotalPaidInterest;
    @FXML TableColumn<LoanDTO, String> lenderTotalAmountLeftToPay;
    @FXML TableColumn<LoanDTO, String> lenderTotalInterestLeftToPay;
    @FXML TableColumn<LoanDTO, Integer> lenderDelayedPaymentsNumber;
    @FXML TableColumn<LoanDTO, String> lenderDelayedPaymentsSum;
    @FXML TableColumn<LoanDTO, Integer> lenderFinishedYaz;

    //transactionTable
    @FXML TableView<TransactionDTO> transactionTable;
    @FXML TableColumn<TransactionDTO, Integer> transactionYazColumn;
    @FXML TableColumn<TransactionDTO, String> transactionAmountColumn;
    @FXML TableColumn<TransactionDTO, String> transactionBeforeColumn;
    @FXML TableColumn<TransactionDTO, String> transactionAfterColumn;



    //Filter:
    @FXML TextField amountTextField;
    @FXML Label amountInvalidLabel;
    @FXML CheckBox categoriesCheckBox;
    @FXML CheckComboBox<String> categoriesCheckComboBox;
    @FXML CheckBox minimumInterestCheckBox;
    @FXML TextField minimumInterestTextField;
    @FXML Label minimumInterestInvalidLabel;
    @FXML CheckBox minimumTotalYazCheckBox;
    @FXML TextField minimumTotalYazTextField;
    @FXML Label minimumTotalYazInvalidLabel;
    @FXML CheckBox maximumOpenLoansCheckBox;
    @FXML TextField maximumOpenLoansTextField;
    @FXML Label maximumOpenLoansInvalidLabel;
    @FXML CheckBox maximumOwnershipCheckBox;
    @FXML TextField maximumOwnershipTextField;
    @FXML Label maximumOwnershipInvalidLabel;

    @FXML ProgressBar scrambleProgressBar;
    @FXML Label scrambleProgressPercent;

    //Scramble:
    @FXML GridPane scrambleGridPane;
    @FXML Label filterResultLabel;

    //Table:
    @FXML TableView <LoanDTO> filteredLoansTable;
    @FXML TableColumn <LoanDTO, String> filteredLoansIdColumn;
    @FXML TableColumn <LoanDTO, String> filteredLoansOwnerColumn;
    @FXML TableColumn <LoanDTO, Double> filteredLoansAmountColumn;
    @FXML TableColumn <LoanDTO, String> filteredLoansCategoryColumn;
    @FXML TableColumn <LoanDTO, Double> filteredLoansInterestColumn;
    @FXML TableColumn <LoanDTO, Integer> filteredLoansTotalYazColumn;
    @FXML TableColumn <LoanDTO, Integer> filteredLoansPaymentRateColumn;
    @FXML TableColumn <LoanDTO, String> filteredLoansStatusColumn;
    @FXML TableColumn <LoanDTO, List<String>> filteredLoansLenderListName;
    @FXML TableColumn <LoanDTO, List<Double>> filteredLoansLenderListAmount;
    @FXML TableColumn<LoanDTO, String> filteredAmountRaised;
    @FXML TableColumn<LoanDTO, String> filteredAmountLeftToActive;

    //Scramble buttons
    @FXML CheckComboBox<String> loansCheckComboBox;
    @FXML Button scrambleButton;
    @FXML Label scrambleInvalidLabel;

    //Payment:
    //Loan Table:
    @FXML TableView<LoanDTO> paymentTable;
    @FXML TableColumn<LoanDTO, String> payLoanIdColumn;
    @FXML TableColumn<LoanDTO, String> payLoanOwnerColumn;
    @FXML TableColumn<LoanDTO, String> payLoanCategoryColumn;
    @FXML TableColumn<LoanDTO, Double> payLoanAmountColumn;
    @FXML TableColumn<LoanDTO, Double> payLoanInterestColumn;
    @FXML TableColumn<LoanDTO, Integer> payLoanYazColumn;
    @FXML TableColumn<LoanDTO, Integer> payLoanPaymentRateColumn;
    @FXML TableColumn<LoanDTO, String> payLoanStatusColumn;
    @FXML TableColumn<LoanDTO, List<String>> payLoansLenderListName;
    @FXML TableColumn<LoanDTO, List<Double>> payLoansLenderListAmount;
    @FXML TableColumn<LoanDTO, Integer> payLoanStartingYazColumn;
    @FXML TableColumn<LoanDTO, Integer> payLoanNextPaymentYazColumn;
    @FXML TableColumn<LoanDTO, List<Integer>> payLoansPaymentYazColumn;
    @FXML TableColumn<LoanDTO, List<Double>> payLoansPaymentAmountColumn;
    @FXML TableColumn<LoanDTO, List<Double>> payLoansPaymentInterestColumn;
    @FXML TableColumn<LoanDTO, List<Double>> payLoansPaymentTotalColumn;
    @FXML TableColumn<LoanDTO, String> payLoanTotalPaidAmount;
    @FXML TableColumn<LoanDTO, String> payLoanTotalPaidInterest;
    @FXML TableColumn<LoanDTO, String> payLoanTotalAmountLeftToPay;
    @FXML TableColumn<LoanDTO, String> payLoanTotalInterestLeftToPay;
    @FXML TableColumn<LoanDTO, Integer> payLoanDelayedPaymentsNumber;
    @FXML TableColumn<LoanDTO, String> payLoanDelayedPaymentsSum;

    //Notification Table:
    @FXML TableView<NotificationDTO> notificationTable;
    @FXML TableColumn<NotificationDTO, String> notificationIdColumn;
    @FXML TableColumn<NotificationDTO, Integer> notificationYazColumn;
    @FXML TableColumn<NotificationDTO, String> notificationTotalSumColumn;


    @FXML ComboBox<LoanDTO> paymentLoanComboBox;
    @FXML TextField paymentLoanTextField;
    @FXML Button payButton;
    @FXML Button closeLoanButton;
    @FXML Label paymentInvalidInput;
    @FXML Label paymentNotEnoughMoneyLabel;

    private Stage primaryStage;
    private ScrollPane scrollPane;
    private Engine engine;
    private MainController mainController;
    private CustomerDTO customer;
    private int currentYaz;

    private List<LoanDTO> lenderList;
    private List<LoanDTO> loanerList;
    private List<TransactionDTO> transactionList;
    private List<NotificationDTO> notificationList;
    private ObservableList<LoanDTO> loanerObservableList;
    private ObservableList<LoanDTO> lenderObservableList;
    private ObservableList<TransactionDTO> transactionObservableList;
    private ObservableList<NotificationDTO> notificationObservableList;
    private SimpleBooleanProperty isFiltered;

    private List<String> categories;
    int investedAmount;
    int maximumLoanOwnership;
    private List<LoanDTO> filteredLoans;
    private ObservableList<LoanDTO> filteredLoansObservableList;

    private List<LoanDTO> loansToPayFor;
    private ObservableList loansToPayForObservableList;



    public CustomerController()  {
        isFiltered=new SimpleBooleanProperty(false);
    }

    @FXML
    private void initialize(){
        loanerTable.setPlaceholder(new Label("You don't borrow from any loan."));
        lenderTable.setPlaceholder(new Label("You don't lend to any loan"));
        filteredLoansTable.setPlaceholder(new Label("There are no loans that match your criteria."));
        notificationTable.setPlaceholder(new Label ("There are no notifications yet."));
        transactionTable.setPlaceholder(new Label ("There are no transactions yet."));
        paymentTable.setPlaceholder(new Label("There are no loans to pay for."));
        bindLoansGeneral(loanerIdColumn, loanerOwnerColumn, loanerAmountColumn, loanerCategoryColumn, loanerInterestColumn, loanerTotalYazColumn, loanerPaymentRateColumn, loanerStatusColumn, loanerLenderListName, loanerLenderListAmount);
        bindLoansGeneral(lenderIdColumn, lenderOwnerColumn, lenderAmountColumn, lenderCategoryColumn, lenderInterestColumn, lenderTotalYazColumn, lenderPaymentRateColumn, lenderStatusColumn, lenderLenderListName, lenderLenderListAmount);
        bindLoansGeneral(filteredLoansIdColumn, filteredLoansOwnerColumn, filteredLoansAmountColumn, filteredLoansCategoryColumn, filteredLoansInterestColumn, filteredLoansTotalYazColumn,
                filteredLoansPaymentRateColumn, filteredLoansStatusColumn, filteredLoansLenderListName, filteredLoansLenderListAmount);
        bindLoansGeneral(payLoanIdColumn, payLoanOwnerColumn, payLoanAmountColumn, payLoanCategoryColumn, payLoanInterestColumn, payLoanYazColumn, payLoanPaymentRateColumn, payLoanStatusColumn, payLoansLenderListName, payLoansLenderListAmount);
        bindLoansPending(loanerAmountRaisedColumn, loanerAmountLeftToActive);
        bindLoansPending(lenderAmountRaisedColumn, lenderAmountLeftToActive);
        bindLoansPending(filteredAmountRaised, filteredAmountLeftToActive);
        bindLoansActiveRisk(loanerStartingYazColumn, loanerNextPaymentYazColumn, loanerPaymentYazColumn, loanerPaymentAmountColumn, loanerPaymentInterestColumn, loanerPaymentTotalColumn,
                loanerTotalPaidAmount, loanerTotalPaidInterest, loanerTotalAmountLeftToPay, loanerTotalInterestLeftToPay, loanerDelayedPaymentsNumber, loanerDelayedPaymentsSum);
        bindLoansActiveRisk(lenderStartingYazColumn, lenderNextPaymentYazColumn, lenderPaymentYazColumn, lenderPaymentAmountColumn, lenderPaymentInterestColumn, lenderPaymentTotalColumn,
                lenderTotalPaidAmount, lenderTotalPaidInterest, lenderTotalAmountLeftToPay, lenderTotalInterestLeftToPay, lenderDelayedPaymentsNumber, lenderDelayedPaymentsSum);
        bindLoansActiveRisk(payLoanStartingYazColumn, payLoanNextPaymentYazColumn, payLoansPaymentYazColumn, payLoansPaymentAmountColumn, payLoansPaymentInterestColumn, payLoansPaymentTotalColumn, payLoanTotalPaidAmount, payLoanTotalPaidInterest, payLoanTotalAmountLeftToPay, payLoanTotalInterestLeftToPay, payLoanDelayedPaymentsNumber, payLoanDelayedPaymentsSum);
        bindLoansFinished(lenderFinishedYaz);
        bindLoansFinished(loanerFinishedYaz);
        transactionYazColumn.setCellValueFactory(new PropertyValueFactory<>("yaz"));
        transactionAmountColumn.setCellValueFactory(new PropertyValueFactory<>("amountWithSign"));
        transactionBeforeColumn.setCellValueFactory(new PropertyValueFactory<>("balanceBeforeString"));
        transactionAfterColumn.setCellValueFactory(new PropertyValueFactory<>("balanceAfterString"));
        categoriesCheckBox.selectedProperty().addListener((e, selectedStatus, d)->{
            categoriesCheckComboBox.setDisable(selectedStatus);
            if(selectedStatus)
                categoriesCheckComboBox.getCheckModel().clearChecks();
        });
        minimumInterestCheckBox.selectedProperty().addListener((e,selectedStatus,d)-> {
            minimumInterestTextField.setDisable(selectedStatus);
            minimumInterestTextField.setText("");
            minimumInterestInvalidLabel.setText("");
        });
        minimumTotalYazCheckBox.selectedProperty().addListener((e,selectedStatus ,d)-> {
            minimumTotalYazTextField.setDisable(selectedStatus);
            minimumTotalYazTextField.setText("");
            minimumTotalYazInvalidLabel.setText("");
        });
        maximumOpenLoansCheckBox.selectedProperty().addListener((e,selectedStatus ,d)-> {
            maximumOpenLoansTextField.setDisable(selectedStatus);
            maximumOpenLoansTextField.setText("");
            maximumOpenLoansInvalidLabel.setText("");
        });
        maximumOwnershipCheckBox.selectedProperty().addListener((e,selectedStatus ,d)-> {
            maximumOwnershipTextField.setDisable(selectedStatus);
            maximumOwnershipTextField.setText("");
            maximumOwnershipInvalidLabel.setText("");
        });

        scrambleGridPane.visibleProperty().bind(isFiltered);
        filterResultLabel.visibleProperty().bind(isFiltered);
        loansCheckComboBox.visibleProperty().bind(isFiltered);
        scrambleButton.visibleProperty().bind(isFiltered);


        Callback<ListView<LoanDTO>, ListCell<LoanDTO>> loanDTOToPayComboBoxFactory = f -> new ListCell<LoanDTO>() {
            @Override
            protected void updateItem(LoanDTO item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setGraphic(null);
                } else {
                    setText(item.getId());
                }
            };
        };
        notificationIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        notificationYazColumn.setCellValueFactory(new PropertyValueFactory<>("yaz"));
        notificationTotalSumColumn.setCellValueFactory(new PropertyValueFactory<>("amountString"));


        paymentLoanComboBox.setButtonCell(loanDTOToPayComboBoxFactory.call(null));
        paymentLoanComboBox.setCellFactory(loanDTOToPayComboBoxFactory);
//        paymentLoanTextField.disableProperty().bind(paymentLoanComboBox.getSelectionModel().getSelectedItem().getStatus().equals("D"));
//        payButton.disableProperty().bind(paymentLoanComboBox.valueProperty().isNull());
        closeLoanButton.disableProperty().bind(paymentLoanComboBox.valueProperty().isNull());
    }

    private void bindLoansGeneral(TableColumn<LoanDTO, String> idColumn, TableColumn<LoanDTO, String> ownerColumn, TableColumn<LoanDTO, Double> amountColumn, TableColumn<LoanDTO, String> categoryColumn, TableColumn<LoanDTO, Double> interestColumn, TableColumn<LoanDTO, Integer> totalYazColumn, TableColumn<LoanDTO, Integer> paymentRateColumn,
                                  TableColumn<LoanDTO, String> statusColumn, TableColumn<LoanDTO, List<String>> loanLendersNameColumn, TableColumn<LoanDTO, List<Double>> loanLendersAmountColumn) {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        ownerColumn.setCellValueFactory(new PropertyValueFactory<>("owner"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("Category"));
        interestColumn.setCellValueFactory(new PropertyValueFactory<>("interestPerPayment"));
        totalYazColumn.setCellValueFactory(new PropertyValueFactory<>("totalYaz"));
        paymentRateColumn.setCellValueFactory(new PropertyValueFactory<>("paysEveryYaz"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        loanLendersNameColumn.setCellValueFactory(new PropertyValueFactory<>("lendersName"));
        loanLendersNameColumn.setCellFactory(column->{
            return new TableCell<LoanDTO, List<String>>() {
                @Override
                protected void updateItem(List<String>item, boolean empty) {
                    super.updateItem(item, empty);
                    if(item==null || empty) {
                        setGraphic(null);
                    } else {
                        VBox vbox = new VBox();
                        for(String name : item){
                            Label label = new Label(name);
                            vbox.getChildren().add(label);
                        }
                        setGraphic(vbox);
                    }
                }
            };
        });
        loanLendersAmountColumn.setCellValueFactory(new PropertyValueFactory<>("lendersAmount"));
        loanLendersAmountColumn.setCellFactory(column->{
            return new TableCell<LoanDTO, List<Double>>() {
                @Override
                protected void updateItem(List<Double>item, boolean empty) {
                    super.updateItem(item, empty);
                    if(item==null || empty) {
                        setGraphic(null);
                    } else {
                        VBox vbox = new VBox();
                        for(Double amount : item){
                            Label label = new Label(String.format("%.1f", amount));
                            vbox.getChildren().add(label);
                        }
                        setGraphic(vbox);
                    }
                }
            };
        });
    }

    private void bindLoansPending(TableColumn<LoanDTO,String> loanAmountRaised, TableColumn<LoanDTO, String> loanAmountLeftToActive){

        loanAmountRaised.setCellValueFactory(param -> {
            if(param.getValue().getStatus().equals("pending"))
                return new SimpleStringProperty(param.getValue().getAmountRaisedString());
            else
                return null;
        });

        loanAmountLeftToActive.setCellValueFactory(param -> {
            if(param.getValue().getStatus().equals("pending"))
                return new SimpleStringProperty(param.getValue().getAmountLeftActiveString());
            else
                return null;
        });
    }


    private void bindLoansActiveRisk(TableColumn<LoanDTO, Integer> loanStartingYazColumn, TableColumn<LoanDTO,Integer> loanNextPaymentYazColumn,
                                     TableColumn<LoanDTO, List<Integer>> loansPaymentYazColumn, TableColumn<LoanDTO, List<Double>> loansPaymentAmountColumn,
                                     TableColumn<LoanDTO, List<Double>> loansPaymentInterestColumn, TableColumn<LoanDTO, List<Double>> loansPaymentTotalColumn,
                                     TableColumn<LoanDTO, String> totalPaidAmount, TableColumn<LoanDTO, String> totalPaidInterest,
                                     TableColumn<LoanDTO, String> totalAmountLeftToPay, TableColumn<LoanDTO, String> totalInterestLeftToPay,
                                     TableColumn<LoanDTO, Integer> loanDelayedPaymentsNumber, TableColumn<LoanDTO, String> loanDelayedPaymentsSum){
        loanStartingYazColumn.setCellValueFactory(param -> {
            if(param.getValue().getStatus().equals("new") || param.getValue().getStatus().equals("pending"))
                return null;
            else
                return new SimpleIntegerProperty(param.getValue().getStartingYaz()).asObject();
        });
        loanNextPaymentYazColumn.setCellValueFactory(param -> {
            if(param.getValue().getStatus().equals("risk") || param.getValue().getStatus().equals("active"))
                return new SimpleIntegerProperty(param.getValue().getNextPaymentYaz()).asObject();
            else
                return null;
        });
        loansPaymentYazColumn.setCellValueFactory(new PropertyValueFactory<>("paymentYaz"));
        loansPaymentYazColumn.setCellFactory(column->{
            return new TableCell<LoanDTO, List<Integer>>() {
                @Override
                protected void updateItem(List<Integer>item, boolean empty) {
                    super.updateItem(item, empty);
                    if(item==null || empty) {
                        setGraphic(null);
                    } else {
                        VBox vbox = new VBox();
                        for(Integer yaz : item){
                            Label label = new Label(yaz.toString());
                            vbox.getChildren().add(label);
                        }
                        setGraphic(vbox);
                    }
                }
            };
        });
        loansPaymentAmountColumn.setCellValueFactory(new PropertyValueFactory<>("paymentAmount"));
        loansPaymentAmountColumn.setCellFactory(column->{
            return new TableCell<LoanDTO, List<Double>>() {
                @Override
                protected void updateItem(List<Double>item, boolean empty) {
                    super.updateItem(item, empty);
                    if(item==null || empty) {
                        setGraphic(null);
                    } else {
                        VBox vbox = new VBox();
                        for(Double amount : item){
                            Label label = new Label(String.format("%.1f", amount));
                            vbox.getChildren().add(label);
                        }
                        setGraphic(vbox);
                    }
                }
            };
        });
        loansPaymentInterestColumn.setCellValueFactory(new PropertyValueFactory<>("paymentInterest"));
        loansPaymentInterestColumn.setCellFactory(column->{
            return new TableCell<LoanDTO, List<Double>>() {
                @Override
                protected void updateItem(List<Double>item, boolean empty) {
                    super.updateItem(item, empty);
                    if(item==null || empty) {
                        setGraphic(null);
                    } else {
                        VBox vbox = new VBox();
                        for(Double interest : item){
                            Label label = new Label(String.format("%.1f", interest));
                            vbox.getChildren().add(label);
                        }
                        setGraphic(vbox);
                    }
                }
            };
        });
        loansPaymentTotalColumn.setCellValueFactory(new PropertyValueFactory<>("paymentAmountWithInterest"));
        loansPaymentTotalColumn.setCellFactory(column->{
            return new TableCell<LoanDTO, List<Double>>() {
                @Override
                protected void updateItem(List<Double>item, boolean empty) {
                    super.updateItem(item, empty);
                    if(item==null || empty) {
                        setGraphic(null);
                    } else {
                        VBox vbox = new VBox();
                        for(Double totalPaid : item){
                            Label label = new Label(String.format("%.1f", totalPaid));
                            vbox.getChildren().add(label);
                        }
                        setGraphic(vbox);
                    }
                }
            };
        });
        totalPaidAmount.setCellValueFactory(param -> {
            if(param.getValue().getStatus().equals("risk") || param.getValue().getStatus().equals("active"))
                return new SimpleStringProperty(param.getValue().getTotalPaidAmountString());
            else
                return null;
        });
        totalPaidInterest.setCellValueFactory(param -> {
            if(param.getValue().getStatus().equals("risk") || param.getValue().getStatus().equals("active"))
                return new SimpleStringProperty(param.getValue().getTotalPaidInterestString());
            else
                return null;
        });
        totalAmountLeftToPay.setCellValueFactory(param -> {
            if(param.getValue().getStatus().equals("risk") || param.getValue().getStatus().equals("active"))
                return new SimpleStringProperty(param.getValue().getTotalAmountLeftToPayString());
            else
                return null;
        });
        totalInterestLeftToPay.setCellValueFactory(param -> {
            if(param.getValue().getStatus().equals("risk") || param.getValue().getStatus().equals("active"))
                return new SimpleStringProperty(param.getValue().getTotalInterestLeftToPayString());
            else
                return null;
        });
        loanDelayedPaymentsNumber.setCellValueFactory(param -> {
            if(param.getValue().getStatus().equals("risk"))
                return new SimpleIntegerProperty(param.getValue().getDelayedPayments().getKey()).asObject();
            else
                return null;
        });
        loanDelayedPaymentsSum.setCellValueFactory(param -> {
            if(param.getValue().getStatus().equals("risk"))
                return new SimpleStringProperty(param.getValue().getDelayedPaymentsAmountString());
            else
                return null;
        });
    }
    private void bindLoansFinished(TableColumn<LoanDTO, Integer> loanFinishedYaz){
        loanFinishedYaz.setCellValueFactory(param -> {
            if(param.getValue().getStatus().equals("finished"))
                return new SimpleIntegerProperty(param.getValue().getFinishYaz()).asObject();
            else
                return null;
        });
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    public void loadFIleButtonAction() throws IOException, CloneNotSupportedException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose an xml file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("xml files", "*.xml"));
        File selectedFile = fileChooser.showOpenDialog(primaryStage);
        if(selectedFile == null) {
            return;
        }

        RequestBody body =
                new MultipartBody.Builder()
                        .addFormDataPart("file1", selectedFile.getName(), RequestBody.create(selectedFile, MediaType.parse("application/xml")))
                        //.addFormDataPart("key1", "value1") // you can add multiple, different parts as needed
                        .build();

        Request request = new Request.Builder()
                .url(UPLOAD_FILE_PAGE)
                .post(body)
                .build();
        HttpClientUtil.runAsync(request, new okhttp3.Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        notification("Error: ", e.getMessage())
                );
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBody = response.body().string();
                if (response.code() != 200) {
                    Platform.runLater(() ->
                            notification("Invalid File", responseBody)
                    );
                } else {
                    Platform.runLater(() -> {

                        CustomerDTO loggedIn=GSON_INSTANCE.fromJson(responseBody, CustomerDTO.class);
                        updateCustomer(loggedIn);

                    });
                }
            }
        });

//        try {
//            engine.loadFile(selectedFile);
//            String absolutePath=selectedFile.getAbsolutePath();
//            mainController.getSelectedFileProperty().set(absolutePath);
//            isFileSelected.set(true);
//            customers= engine.getAllCustomersDetails();
//            loans=engine.getAllLoans();
//            loansList=FXCollections.observableArrayList(loans);
//            customerList=FXCollections.observableArrayList(customers);
//            customerTable.setItems(customerList);
//            loanTable.setItems(loansList);
//            mainController.updateSystem(customers);
//            xmlMessage("Success!", "File loaded successfully!");
//        }
//        catch(NameException e ) {
//            xmlMessage("InvalidFile", "There are 2 different instances of the " + e.getType() + " " + e.getName());
//        }
//        catch (LoanFieldDoesNotExist e) {
//            xmlMessage("InvalidFile", "The loan " + e.getLoanID() + " contains the " + e.getFieldType() + " " + e.getName() +
//                    ", but the " + e.getFieldType() + " " + e.getName() + " doesn't exist.");
//        }
//        catch (YazException e) {
//            xmlMessage("InvalidFile", "For the loan: " + e.getLoanID() + ", the total loan time (" + e.getTotalYaz() +
//                    ") isn't divided by the payment rate (" + e.getPaymentRate() + ")");
//        }

    }


    public ScrollPane getScrollPane() {
        return scrollPane;
    }

    public void setScrollPane(ScrollPane scrollPane) {
        this.scrollPane = scrollPane;
    }

    public Engine getEngine() {
        return engine;
    }

    public void setEngine(Engine engine) {
        this.engine = engine;
        categories=engine.getCategories();
        categoriesCheckComboBox.getItems().setAll(categories);
    }

    public MainController getMainController() {
        return mainController;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public CustomerDTO getCustomer() {
        return customer;
    }

    public void updateCustomer(CustomerDTO customer) {
        this.customer = customer;
        loanerList=customer.getOwnerDTO();
        loanerObservableList =FXCollections.observableArrayList(loanerList);
        loanerTable.setItems(loanerObservableList);

        lenderList=customer.getLenderDTO();
        lenderObservableList =FXCollections.observableArrayList(lenderList);
        lenderTable.setItems(lenderObservableList);

        transactionList=customer.getTransactionDTOList();
        transactionObservableList=FXCollections.observableArrayList(transactionList);
        transactionTable.setItems(transactionObservableList);

        StringExpression s= Bindings.concat("Balance: ", String.format("%.1f", customer.getCurrentAmount()));
        balanceLabel.textProperty().bind(s);
        if(filteredLoans!=null) {
            filteredLoans.clear();
            filteredLoansObservableList.clear();
        }
        amountTextField.setText("");
        categoriesCheckBox.setSelected(false);
        minimumInterestCheckBox.setSelected(false);
        minimumTotalYazCheckBox.setSelected(false);
        maximumOpenLoansCheckBox.setSelected(false);
        maximumOwnershipCheckBox.setSelected(false);
        loansCheckComboBox.getCheckModel().clearChecks();
        isFiltered.set(false);
        scrambleProgressBar.setVisible(false);
        scrambleProgressPercent.setText("");

        notificationList=customer.getNotificationDTOList();
        notificationObservableList=FXCollections.observableArrayList(notificationList);
        notificationTable.setItems(notificationObservableList);

        loansToPayFor=loanerList.stream().
                filter(loan->loan.getStatus().equals("risk") || (loan.getStatus().equals("active"))).
                collect(Collectors.toList());
        loansToPayForObservableList=FXCollections.observableArrayList(loansToPayFor);
        paymentTable.setItems(loansToPayForObservableList);
        //test.set("");
        paymentLoanTextField.disableProperty().set(true);
        payButton.disableProperty().set(true);
        paymentLoanComboBox.getSelectionModel().clearSelection();
        paymentLoanComboBox.getItems().clear();
        paymentLoanComboBox.getItems().setAll(loansToPayFor);
//        paymentLoanTextField.setDisable(true);



        clearInvalid();
    }

    @FXML
    public void chargeButtonAction() {
        String input=chargeTextField.getText();
        try {
            int integer=Integer.parseInt(input);
            if(integer <= 0 )
                notification("Invalid Input", integer + " is not a positive integer");
            else {
                engine.charge(customer.getName(), integer);
                notification("Charging Succeeded!", integer + " were added to your account");
                chargeTextField.clear();
                updateCustomer(engine.createCustomerDTO(customer.getName()));
                mainController.updateCustomersAdmin();
            }

        }
        catch (NumberFormatException e){
            notification("Invalid Input", input+  " is not an integer");
        }
        chargeTextField.clear();
    }

    public void withdrawButtonAction() {
        String input=withdrawTextField.getText();
        try {
            int integer=Integer.parseInt(input);
            if(integer <= 0 )
                notification("Invalid Input", integer + " is not a positive integer.");
            else if(integer > customer.getCurrentAmount())
                notification("Invalid Input", integer + " is more than the balance in your account (" +
                        customer.getCurrentAmount() + ").");
            else {
                engine.withdraw(customer.getName(), integer);
                notification("Withdrawing Succeeded!", integer + " were withdrawn your account.");
                withdrawTextField.clear();
                updateCustomer(engine.createCustomerDTO(customer.getName()));
                mainController.updateCustomersAdmin();
            }

        }
        catch (NumberFormatException e){
            notification("Invalid Input", input+  " is not an integer.");
        }
        chargeTextField.clear();
    }


    private void notification(String title, String message) {
        Notifications.create()
                .title(title)
                .text(message)
                .hideAfter(Duration.seconds(5))
                .position(Pos.CENTER)
                .showWarning();
    }

    @FXML
    public void filter() {
        clearInvalid();
        int amount= filterAmount();
        Set<String> categories= new HashSet<>(categoriesCheckComboBox.getCheckModel().getCheckedItems());
        int interest= filterField(minimumInterestTextField, minimumInterestInvalidLabel);
        int yaz= filterField(minimumTotalYazTextField, minimumTotalYazInvalidLabel);
        int maximumOpenLoans= filterField(maximumOpenLoansTextField, maximumOpenLoansInvalidLabel);
        if(maximumOpenLoans==0)
            maximumOpenLoans=Integer.MAX_VALUE;
        int maximumOwnership= filterMaximumOwnership();
        if(amount ==INVALID || interest==INVALID || yaz== INVALID || maximumOpenLoans==INVALID ||maximumOwnership==INVALID){
            return;
        }
        maximumLoanOwnership=maximumOwnership;
        investedAmount=amount;
        scrambleProgressBar.setVisible(true);
        ScrambleTask task=new ScrambleTask(engine, customer.getName(), categories, interest,yaz, maximumOpenLoans);
        scrambleProgressBar.progressProperty().bind(task.progressProperty());
        Thread thread= new Thread(task);
        taskToProgress(task,()-> {isFiltered.set(true);
        });
        thread.start();
        task.valueProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue!=null)
                filteredLoans=newValue;
        });


        filteredLoans=engine.filteredLoans(customer.getName(), categories, interest, yaz, maximumOpenLoans);
        filteredLoansObservableList = FXCollections.observableArrayList(filteredLoans);
        filteredLoansTable.setItems(filteredLoansObservableList);
        loansCheckComboBox.getItems().clear();
        loansCheckComboBox.getCheckModel().clearChecks();
        loansCheckComboBox.getItems().setAll(filteredLoans.stream().map(LoanDTO::getId).collect(Collectors.toList()));
        if(filteredLoans.isEmpty()){
            loansCheckComboBox.setDisable(true);
            scrambleButton.setDisable(true);
        }
        else {
            loansCheckComboBox.setDisable(false);
            scrambleButton.setDisable(false);
        }
    }

    public void taskToProgress(ScrambleTask filteredNewLoans, Runnable onFinish){
        scrambleProgressBar.progressProperty().bind(filteredNewLoans.progressProperty());
        scrambleProgressPercent.textProperty().bind(
                Bindings.concat(
                        Bindings.format(
                                "%.0f",
                                Bindings.multiply(
                                        filteredNewLoans.progressProperty(),
                                        100)),
                        " %"));
        filteredNewLoans.valueProperty().addListener((observable, oldValue, newValue) -> taskFinished(Optional.ofNullable(onFinish)));
    }

    public void taskFinished(Optional<Runnable> onFinish) {
        this.scrambleProgressPercent.textProperty().unbind();
        this.scrambleProgressBar.progressProperty().unbind();
        onFinish.ifPresent(Runnable::run);
    }


    private int filterAmount(){
        try{
            int amount=Integer.parseInt(amountTextField.getText());
            if(amount<=0) {
                amountInvalidLabel.setText("must be a positive integer");
                return INVALID;
            }
            if(amount > customer.getCurrentAmount()) {
                amountInvalidLabel.setText("Not enough money in your account.");
                return INVALID;
            }
            return amount;
        }
        catch(NumberFormatException e) {
            if (amountTextField.getText().equals(""))
                amountInvalidLabel.setText("This field is mandatory.");
            else{
                amountInvalidLabel.setText("That's not an integer.");
            }
            return INVALID;
        }
    }

    private int filterField(TextField textField, Label invalidLabel) {
        if(textField.isDisabled())
            return 0;
        try {
            int interest=Integer.parseInt(textField.getText());
            if(interest<=0) {
                invalidLabel.setText("must be a positive integer.");
                return INVALID;
            }
            return interest;
        }
        catch(NumberFormatException e) {
            invalidLabel.setText("that's not an integer.");
            return INVALID;
        }
    }

    private int filterMaximumOwnership(){
        if (maximumOwnershipTextField.isDisabled())
            return 100;
        try {
            int Ownership = Integer.parseInt(maximumOwnershipTextField.getText());
            if (Ownership <= 0 || Ownership>100) {
                maximumOwnershipInvalidLabel.setText("must be an integer between 1 to 100.");
                return INVALID;
            }
            return Ownership;
        } catch (NumberFormatException e) {
            maximumOwnershipInvalidLabel.setText("that's not an integer.");
            return INVALID;
        }
    }

    @FXML
    public void scrambleButtonAction() {
        if(loansCheckComboBox.getCheckModel().isEmpty()){
            scrambleInvalidLabel.setText("must choose loans");
            return;
        }
        double invested=engine.assign(loansCheckComboBox.getCheckModel().getCheckedItems(), investedAmount, customer.getName(), maximumLoanOwnership);
        if(invested<investedAmount)
            notification("Scramble succeeded, but...", "only " + String.format("%.1f",invested) + " was invested");
        else
            notification("Scramble succeeded!", "Successfully invested " + investedAmount + "");

        mainController.updateAll();
    }

    private void clearInvalid(){
        amountInvalidLabel.setText("");
        minimumInterestInvalidLabel.setText("");
        minimumTotalYazInvalidLabel.setText("");
        maximumOpenLoansInvalidLabel.setText("");
        maximumOwnershipInvalidLabel.setText("");
        scrambleInvalidLabel.setText("");
        paymentNotEnoughMoneyLabel.setText("");
        paymentInvalidInput.setText("");
        paymentLoanTextField.setText("");
    }

    @FXML
    public void paymentComboBoxAction() {
        if(!paymentLoanComboBox.getSelectionModel().isEmpty()) {
            if (paymentLoanComboBox.getSelectionModel().getSelectedItem().getStatus().equals("risk")) {
                paymentLoanTextField.disableProperty().set(false);
                payButton.disableProperty().set(false);
            } else if (paymentLoanComboBox.getSelectionModel().getSelectedItem().isPayingPeriod()) {
                payButton.disableProperty().set(false);
            }
        }
    }


    @FXML
    public void closeLoanButtonAction() {
        if(paymentLoanComboBox.getSelectionModel().getSelectedItem().getAmountLeftToPay()>customer.getCurrentAmount()){
            paymentNotEnoughMoneyLabel.setText("Not enough money in your account!");
            return;
        }
        engine.closeLoan(paymentLoanComboBox.getSelectionModel().getSelectedItem().getId());
        mainController.updateAll();
        notification("Payment succeeded.", "The loan has been closed.");
    }
    @FXML
    public void payButtonAction(){
        if(paymentLoanComboBox.getSelectionModel().getSelectedItem().getStatus().equals("active") || paymentLoanTextField.getText().equals("")){
            if(paymentLoanComboBox.getSelectionModel().getSelectedItem().getExpectedAmount()>customer.getCurrentAmount()){
                paymentNotEnoughMoneyLabel.setText("Not enough money in your account!");
                return;
            }
            notification("Success!", "Payment had been made.");
            engine.payLoan(paymentLoanComboBox.getSelectionModel().getSelectedItem().getId());
            mainController.updateAll();
        }
        else { //status is risk and text isn't empty...
            try {
                double amount = Double.parseDouble(paymentLoanTextField.getText());
                if(amount<=0.0) {
                    paymentInvalidInput.setText("Must be a positive number.");
                    return;
                }
                if(amount>paymentLoanComboBox.getSelectionModel().getSelectedItem().getAmountLeftToPay()+
                        paymentLoanComboBox.getSelectionModel().getSelectedItem().getInterestLeftToPay()){
                    paymentInvalidInput.setText("That's higher than the amount left to pay");
                    return;
                }
                if(amount>customer.getCurrentAmount()) {
                    paymentInvalidInput.setText("Not enough money.");
                    return;
                }
                engine.payLoanRisk(paymentLoanComboBox.getSelectionModel().getSelectedItem().getId(), amount);
                notification("Payment succeeded.", "Paid " + paymentLoanTextField.getText());
                mainController.updateAll();
            }
            catch(NumberFormatException e) {
                paymentInvalidInput.setText("That's not a number!");
            }
        }

    }
}
