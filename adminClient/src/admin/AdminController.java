package admin;

import com.google.gson.reflect.TypeToken;
import dto.customerDTO.CustomerDTO;
import dto.database.AdminDatabase;
import dto.loanDTO.LoanDTO;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableIntegerValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;
import okhttp3.*;
import org.controlsfx.control.Notifications;
import org.jetbrains.annotations.NotNull;
import util.HttpClientUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

import static util.Constants.*;

public class AdminController  {

    @FXML Button increaseYazButton;

    @FXML TableView <CustomerDTO> customerTable;

    @FXML ScrollPane customerScrollPane;
    @FXML TableColumn <CustomerDTO, String> customerNameColumn;
    @FXML TableColumn <CustomerDTO, Double> customerBalanceColumn;
    @FXML TableColumn<CustomerDTO, Map<String, Integer>> customerBorrowerColumn;
    @FXML TableColumn<CustomerDTO, Map<String, Integer>> customerLenderColumn;

    @FXML ScrollPane loanScrollPane;
    @FXML TableView <LoanDTO> loanTable;
    @FXML TableColumn<LoanDTO, String> loanIdColumn;
    @FXML TableColumn<LoanDTO, String> loanOwnerColumn;
    @FXML TableColumn<LoanDTO, String> loanCategoryColumn;
    @FXML TableColumn<LoanDTO, Double> loanAmountColumn;
    @FXML TableColumn<LoanDTO, Double> loanInterestColumn;
    @FXML TableColumn<LoanDTO, Integer> loanYazColumn;
    @FXML TableColumn<LoanDTO, Integer> loanPaymentRateColumn;
    @FXML TableColumn<LoanDTO, String> loanStatusColumn;
    @FXML TableColumn<LoanDTO, List<String>> loanLendersNameColumn;
    @FXML TableColumn<LoanDTO, List<Double>> loanLendersAmountColumn;
    @FXML TableColumn<LoanDTO, String> loanAmountRaised;
    @FXML TableColumn<LoanDTO, String> loanAmountLeftToActive;
    @FXML TableColumn<LoanDTO, Integer> loanStartingYazColumn;
    @FXML TableColumn<LoanDTO, Integer> loanNextPaymentYazColumn;
    @FXML TableColumn<LoanDTO, List<Integer>> loansPaymentYazColumn;
    @FXML TableColumn<LoanDTO, List<Double>> loansPaymentAmountColumn;
    @FXML TableColumn<LoanDTO, List<Double>> loansPaymentInterestColumn;
    @FXML TableColumn<LoanDTO, List<Double>> loansPaymentTotalColumn;
    @FXML TableColumn<LoanDTO, String> totalPaidAmount;
    @FXML TableColumn<LoanDTO, String> totalPaidInterest;
    @FXML TableColumn<LoanDTO, String> totalAmountLeftToPay;
    @FXML TableColumn<LoanDTO, String> totalInterestLeftToPay;
    @FXML TableColumn<LoanDTO, Integer> loanDelayedPaymentsNumber;
    @FXML TableColumn<LoanDTO, String> loanDelayedPaymentsSum;
    @FXML TableColumn<LoanDTO, Integer> loanFinishedYaz;

    @FXML private Label yazLabel;
    @FXML private Label AdminNameLabel;

    private Stage primaryStage;
    //private MainController mainController;
    private int currentYaz;
    private SimpleIntegerProperty observableYaz=new SimpleIntegerProperty();
    private List<CustomerDTO> customers;
    private List<LoanDTO> loans;
    private ScrollPane root;
    ObservableList<CustomerDTO> customerList;
    ObservableList<LoanDTO> loansList;
    private TimerTask adminRefresher;
    private Timer timer;

    @FXML private Label ReadOnlyLabel;
    @FXML private Slider RewindSlider;
    @FXML private Label SliderValue;
    private boolean isRewind;

    @FXML
    private void initialize() {
        currentYaz = 1;
        observableYaz.set(currentYaz);
        customerList=FXCollections.observableArrayList(new ArrayList<>());
        loansList=FXCollections.observableArrayList(new ArrayList<>());
        RewindSlider.maxProperty().bind(observableYaz.asObject());
        RewindSlider.setMajorTickUnit(1);
        RewindSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                SliderValue.setText(Integer.toString(newValue.intValue()));

            }
        });
        RewindSlider.valueProperty().setValue(observableYaz.getValue());
        yazLabel.textProperty().bind(Bindings.concat("Current Yaz: " + currentYaz));
        loanTable.setItems(loansList);
       customerTable.setItems(customerList);

        //customer binding
        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        customerBalanceColumn.setCellValueFactory(new PropertyValueFactory<>("currentAmount"));
        customerBorrowerColumn.setCellValueFactory(new PropertyValueFactory<>("ownerStatusCount"));
        customerBorrowerColumn.setCellFactory(column->{
            return new TableCell<CustomerDTO, Map<String, Integer>>() {
                @Override
                protected void updateItem(Map<String, Integer> item, boolean empty) {
                    super.updateItem(item, empty);
                    if(item==null || empty) {
                        setGraphic(null);
                    } else {
                        VBox vbox = new VBox();
                        List<String> textList = new LinkedList<>();
                        for(Map.Entry<String, Integer> statusCount : item.entrySet()){
                            String string= statusCount.getKey() + ": " + statusCount.getValue();
                            Label label = new Label(string);
                            vbox.getChildren().add(label);

                        }
                        setGraphic(vbox);
                    }
                }
            };
        });
        customerLenderColumn.setCellValueFactory(new PropertyValueFactory<>("lenderStatusCount"));
        customerLenderColumn.setCellFactory(column->{
            return new TableCell<CustomerDTO, Map<String, Integer>>() {
                @Override
                protected void updateItem(Map<String, Integer> item, boolean empty) {
                    super.updateItem(item, empty);
                    if(item==null || empty) {
                        setGraphic(null);
                    } else {
                        VBox vbox = new VBox();
                        for(Map.Entry<String, Integer> statusCount : item.entrySet()){
                            String string= statusCount.getKey() + ": " + statusCount.getValue();
                            Label label = new Label(string);
                            vbox.getChildren().add(label);

                        }
                        setGraphic(vbox);
                    }
                }
            };
        });

        //loan binding
        loanIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        loanOwnerColumn.setCellValueFactory(new PropertyValueFactory<>("owner"));
        loanAmountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        loanCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        loanInterestColumn.setCellValueFactory(new PropertyValueFactory<>("interestPerPayment"));
        loanYazColumn.setCellValueFactory(new PropertyValueFactory<>("totalYaz"));
        loanPaymentRateColumn.setCellValueFactory(new PropertyValueFactory<>("paysEveryYaz"));
        loanStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
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
        loanFinishedYaz.setCellValueFactory(param -> {
            if(param.getValue().getStatus().equals("finished"))
                return new SimpleIntegerProperty(param.getValue().getFinishYaz()).asObject();
            else
                return null;
        });
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public ScrollPane getRoot() {
        return root;
    }

//    public MainController getMainController() {
//        return mainController;
//    }

//    public void setMainController(MainController mainController) {
//        this.mainController = mainController;
//    }

    public void setRoot(ScrollPane root) {
        this.root = root;
    }

    public List<CustomerDTO> getCustomers() {
        return customers;
    }

    @FXML
    private void increaseYazButtonAction() {
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url(INCREASE_YAZ_PAGE)
                .post(body)
                .build();
        HttpClientUtil.runAsync(request, new okhttp3.Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBody = response.body().string();
                Platform.runLater(() -> {
                    AdminDatabase data = GSON_INSTANCE.fromJson(responseBody, AdminDatabase.class);
                    customers=data.getCustomerList();
                    loans=data.getLoanList();
                    currentYaz =data.getYaz();
                    observableYaz.set(currentYaz);
                    RewindSlider.valueProperty().setValue(observableYaz.getValue());
                    isRewind = data.isRewind();
                    updateAll();
                });
            }
        });
    }

    public void updateCustomers() {
        customerList.clear();
        customerList.addAll(customers);
    }

    public void updateAll() {
        yazLabel.textProperty().bind(Bindings.concat("Current Yaz: " + currentYaz));
        updateCustomers();
        updateLoans();
    }

    public void updateLoans() {
        loansList.clear();
        loansList.addAll(loans);
    }

    public void startRefresh() {
        adminRefresher = new AdminRefresher(
                this::updateSystemPull);
        timer = new Timer();
        timer.schedule(adminRefresher, REFRESH_RATE, REFRESH_RATE);
    }

    public void updateSystemPull(Pair<List<CustomerDTO>, List<LoanDTO>> system) {
        Platform.runLater(() -> {
            customers=system.getKey();
            loans=system.getValue();
            updateAll();
        });
    }
    @FXML
    public void rewindSliderMouseRelease() {
        int yaz=(int)RewindSlider.getValue();
        RequestBody body=RequestBody.create(MediaType.parse("application/json"), GSON_INSTANCE.toJson(yaz));
        Request request = new Request.Builder()
                .url(REWIND_PAGE)
                .post(body)
                .build();
        HttpClientUtil.runAsync(request, new okhttp3.Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                notification("Error:", "Something went wrong: " + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBody = response.body().string();
                Platform.runLater(() -> {
                    AdminDatabase data = GSON_INSTANCE.fromJson(responseBody, AdminDatabase.class);
                    customers = data.getCustomerList();
                    loans = data.getLoanList();
                    currentYaz = data.getYaz();
                    isRewind = data.isRewind();
                    //observableYaz.set(currentYaz);
                    updateAll();
                    increaseYazButton.disableProperty().set(isRewind);
                    ReadOnlyLabel.visibleProperty().set(isRewind);
                });
            }
        });

    }

    public void setAdminNameLabel(String adminNameLabel) {
        AdminNameLabel.setText("View By: "+adminNameLabel);
    }

    private void notification(String title, String message) {
        Notifications.create()
                .title(title)
                .text(message)
                .hideAfter(Duration.seconds(5))
                .position(Pos.CENTER)
                .showWarning();
    }
}
