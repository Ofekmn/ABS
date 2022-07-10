package login;

import admin.AdminController;
import com.sun.istack.internal.NotNull;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;
import okhttp3.*;
import org.controlsfx.control.Notifications;
import util.Constants;
import util.HttpClientUtil;

import java.io.IOException;
import java.net.URL;

public class LoginAdminController {

    private Stage primaryStage;

    @FXML
    private TextField NameTF;
    @FXML
    private Label NameErrorLabel;

    @FXML
    void LoginBtnAction(ActionEvent event) {

        String adminName = NameTF.getText();
        if (adminName.isEmpty()) {
            NameErrorLabel.setText("name filed is empty. You can't login without a name");
            return;
        }

        String finalUrl = HttpUrl
                .parse(Constants.LOGIN_PAGE)
                .newBuilder()
                .addQueryParameter("username", "admin")
                .build()
                .toString();
        Request request = new Request.Builder()
                .url(finalUrl)
                .build();
        HttpClientUtil.runAsync(request, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        notification("Error","Something went wrong: " + e.getMessage())
                );
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    Platform.runLater(() ->
                            notification("Error","You already logged in to the admin with another window.")
                    );
                } else {
                    Platform.runLater(() -> {
                        try {
                            FXMLLoader loader = new FXMLLoader();
                            URL url = getClass().getResource("/admin/admin.fxml");
                            loader.setLocation(url);
                            ScrollPane scrollPane= loader.load(url.openStream());
                            AdminController adminController=loader.getController();
                            adminController.setAdminNameLabel(NameTF.getText());
                            adminController.startRefresh();
                            primaryStage.setTitle("A.B.S Admin Window");
                            Scene scene=new Scene(scrollPane, 1280, 800);
                            primaryStage.setScene(scene);
                            primaryStage.show();
                        } catch (IOException e) {
                            notification("Error",e.getMessage());
                        }
                    });
                }
            }
        });
    }

    private void notification(String title, String message) {
        Notifications.create()
                .title(title)
                .text(message)
                .hideAfter(Duration.seconds(5))
                .position(Pos.CENTER)
                .showWarning();
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
}
