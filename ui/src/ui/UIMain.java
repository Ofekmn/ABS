package ui;

import engine.Engine;
import engine.EngineImpl;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import ui.admin.AdminController;

import java.net.URL;

public class UIMain extends Application {
    public static void main(String[] args) {
//        UI ui=new UI();
//        ui.menu();
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader();

        // load fxml
        URL url=getClass().getResource("app.fxml");
        loader.setLocation(url);
        ScrollPane emptyScrollPane=loader.load(url.openStream());

        loader = new FXMLLoader();
        url = getClass().getResource("main.fxml");
        loader.setLocation(url);
        BorderPane root = loader.load(url.openStream());
        MainController mainController=loader.getController();

        loader = new FXMLLoader();
        url = getClass().getResource("admin/admin.fxml");
        loader.setLocation(url);
        ScrollPane scrollPane= loader.load(url.openStream());
        AdminController adminController=loader.getController();





//        // wire up controller

        Engine engine = new EngineImpl();
        mainController.setPrimaryStage(primaryStage);
        mainController.setEngine(engine);
        adminController.setEngine(engine);
        adminController.setPrimaryStage(primaryStage);
        mainController.setAdminController(adminController);
        adminController.setMainController(mainController);

        adminController.setRoot(scrollPane);

        // set stage

        primaryStage.setTitle("A.B.S");
        mainController.setRoot(root);
        root.setCenter(scrollPane);
        emptyScrollPane.setContent(root);
        Scene scene=new Scene(emptyScrollPane, 1280, 800);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}


