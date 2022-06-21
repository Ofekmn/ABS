package ui;

import dto.customerDTO.CustomerDTO;
import engine.Engine;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import ui.admin.AdminController;
import ui.customer.CustomerController;
import ui.login.LoginController;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MainController {
    @FXML Label selectedFileName;
    @FXML ComboBox viewCB;
    @FXML Label yazLabel;

    private GridPane loginComponent;
    private LoginController loginController;
    private AdminController adminController;
    private Map<String, CustomerController> customerControllerMap;
    private BorderPane root;
    private Stage primaryStage;
    private Engine engine;
    private int currentYaz;
    private SimpleStringProperty selectedFileProperty;
    private ObservableList<String> list;


    public MainController() throws IOException {
        selectedFileProperty = new SimpleStringProperty();
        customerControllerMap= new HashMap<>();
        list=FXCollections.observableArrayList("Admin");
        currentYaz=1;
    }


    @FXML
    private void initialize(){
        selectedFileName.textProperty().bind(selectedFileProperty);
        //;
        viewCB.setValue("Admin");
        viewCB.setItems(list);
        yazLabel.textProperty().bind(Bindings.concat("Current Yaz: " + currentYaz));
    }

    public void setList(ObservableList<String> list) { this.list = list; }


    public Map<String, CustomerController> getCustomerControllerMap() {
        return customerControllerMap;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public SimpleStringProperty getSelectedFileProperty() {
        return selectedFileProperty;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public Engine getEngine() {
        return engine;
    }

    public void setEngine(Engine engine) {
        this.engine = engine;
    }


    public void setAdminController(AdminController adminController) {
        this.adminController = adminController;
    }

    public AdminController getAdminController() { return adminController; }

    public ObservableList<String> getList() { return list; }

    public void setRoot(BorderPane root) { this.root = root; }

    public void updateSystem(List <CustomerDTO> customers) throws IOException, CloneNotSupportedException {
        currentYaz=1;
        yazLabel.textProperty().bind(Bindings.concat("Current Yaz: " + currentYaz));
        ObservableList<String> list=FXCollections.observableArrayList(customers.stream()
                .map(CustomerDTO::getName).
                collect(Collectors.toList()));
        list=list.sorted();
        getList().removeIf(t->!t.equals("Admin"));
        getList().addAll(list);
        customerControllerMap.clear();

        for(CustomerDTO customer : customers) {
            createCustomerController(customer);
        }
    }

    public void createCustomerController(CustomerDTO customer) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        URL url = getClass().getResource("customer/customer.fxml");
        loader.setLocation(url);
        ScrollPane scrollPane = loader.load(url.openStream());
        CustomerController customerController = loader.getController();
        customerController.updateCustomer(customer);
        customerController.setMainController(this);
        customerController.setEngine(engine);
        customerController.setScrollPane(scrollPane);
        customerControllerMap.put(customer.getName(), customerController);
    }

    @FXML
    public void switchView() {
        if(viewCB.getValue().equals("Admin")){
            root.setCenter(adminController.getRoot());
        }
        else {
            root.setCenter(customerControllerMap.get(viewCB.getValue()).getScrollPane());
        }
    }

    public void updateCustomersAdmin() {
        adminController.updateCustomers();
    }
    public void updateAll(){
        currentYaz=engine.getCurrentYaz();
        yazLabel.textProperty().bind(Bindings.concat("Current Yaz: " + currentYaz));
        adminController.updateAll();
        for (String name: customerControllerMap.keySet()) {
            customerControllerMap.get(name).updateCustomer(engine.createCustomerDTO(name));
        }
    }

    public void loadLoginPage() {
        URL loginPageUrl = getClass().getResource("login/login.fxml");
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(loginPageUrl);
            loginComponent = fxmlLoader.load();
            loginController = fxmlLoader.getController();
            loginController.setMainController(this);
            root.setCenter(loginComponent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void setCurrentYaz(int currentYaz) {
        this.currentYaz = currentYaz;
    }
}
