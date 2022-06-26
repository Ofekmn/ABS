import admin.AdminController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;

import java.net.URL;

public class adminMain extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        URL url = getClass().getResource("admin/admin.fxml");
        loader.setLocation(url);
        ScrollPane scrollPane= loader.load(url.openStream());
        AdminController adminController=loader.getController();
        primaryStage.setTitle("A.B.S Admin Window");
        Scene scene=new Scene(scrollPane, 1280, 800);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
