
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import login.LoginController;

import java.io.IOException;
import java.net.URL;

public class customerMain extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        URL loginPageUrl = getClass().getResource("login/login.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(loginPageUrl);
        Parent loginComponent = fxmlLoader.load();
        LoginController loginController = fxmlLoader.getController();
        loginController.setPrimaryStage(primaryStage);
        Scene scene=new Scene(loginComponent,350,305);
        primaryStage.setTitle("A.B.S Customer Window");
        primaryStage.setX(500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
