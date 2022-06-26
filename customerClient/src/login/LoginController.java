package login;

import com.sun.istack.internal.NotNull;
import dto.customerDTO.CustomerDTO;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import okhttp3.*;
import customer.CustomerController;
import util.Constants;
import util.HttpClientUtil;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static util.Constants.GSON_INSTANCE;

public class LoginController {
    @FXML
    public TextField userNameTextField;

    @FXML
    public Label errorMessageLabel;

    private Stage primaryStage;
    private Map<String, CustomerController> customerControllerMap;

    //private MainController mainController;

    private final StringProperty errorMessageProperty = new SimpleStringProperty();

    @FXML
    public void initialize() {
        customerControllerMap= new HashMap<>();
        errorMessageLabel.textProperty().bind(errorMessageProperty);
    }

    @FXML
    private void loginButtonClicked(ActionEvent event) {

        String userName = userNameTextField.getText();
        if (userName.isEmpty()) {
            errorMessageProperty.set("User name is empty. You can't login with empty user name");
            return;
        }

        //noinspection ConstantConditions
        String finalUrl = HttpUrl
                .parse(Constants.LOGIN_PAGE)
                .newBuilder()
                .addQueryParameter("username", userName)
                .build()
                .toString();
        Request request = new Request.Builder()
                .url(finalUrl)
                .build();
        HttpClientUtil.runAsync(request, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        errorMessageProperty.set("Something went wrong: " + e.getMessage())
                );
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBody = response.body().string();
                if (response.code() != 200) {
                    Platform.runLater(() ->
                            errorMessageProperty.set("Something went wrong: " + responseBody)
                    );
                } else {
                    Platform.runLater(() -> {
                        CustomerDTO loggedIn=GSON_INSTANCE.fromJson(responseBody, CustomerDTO.class);
                        try {
                            ScrollPane customer=createCustomerController(loggedIn);
                            Scene scene=new Scene(customer,1280,750);
                            primaryStage.setScene(scene);
                            primaryStage.show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        });
    }

    @FXML
    private void userNameKeyTyped(KeyEvent event) {
        errorMessageProperty.set("");
    }

    @FXML
    private void quitButtonClicked(ActionEvent e) {
        Platform.exit();
    }

    public ScrollPane createCustomerController(CustomerDTO customer) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        URL url = getClass().getResource("/customer/customer.fxml");
        loader.setLocation(url);
        ScrollPane scrollPane = loader.load(url.openStream());
        CustomerController customerController = loader.getController();
        customerController.updateCustomer(customer);
        customerController.setLoginController(this);
        //customerController.setEngine(engine);
        customerController.setScrollPane(scrollPane);
        customerController.setPrimaryStage(primaryStage);
        customerControllerMap.put(customer.getName(), customerController);
        return scrollPane;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
}
