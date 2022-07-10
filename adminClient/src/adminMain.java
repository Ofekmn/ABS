import admin.AdminController;
import com.sun.istack.internal.NotNull;
import dto.customerDTO.CustomerDTO;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import login.LoginAdminController;
import okhttp3.*;
import org.controlsfx.control.Notifications;
import util.HttpClientUtil;

import java.io.IOException;
import java.net.URL;

public class adminMain extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        URL url = getClass().getResource("login/login.fxml");
        loader.setLocation(url);
        ScrollPane scrollPane= loader.load(url.openStream());
        LoginAdminController loginAdminController=loader.getController();
        loginAdminController.setPrimaryStage(primaryStage);
        primaryStage.setTitle("A.B.S Login Admin Window");
        Scene scene=new Scene(scrollPane, 505, 405);
        primaryStage.setScene(scene);
        primaryStage.show();

    }
}
