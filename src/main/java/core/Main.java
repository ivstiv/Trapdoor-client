package core;

import communication.security.AES;
import data.JsonSerializable;
import data.SavedConnection;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import controllers.MainController;

import java.util.Random;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        SavedConnection con = new SavedConnection("164.132.56.199",1234,"Ivan","");
        String ser = con.toString();
        System.out.println(ser);
        SavedConnection con2 = SavedConnection.buildFromJson(ser);
        System.out.println(con2.toString());


        final MainController mainController = new MainController(primaryStage);

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/fxml/main.fxml"));
        fxmlLoader.setController(mainController);
        Parent root = fxmlLoader.load();
        primaryStage.setResizable(true);
        //remove window decoration
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setTitle("Trapdoor");
        primaryStage.setScene(new Scene(root, 1280, 720));
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent event) {
                mainController.stop(); // sends a stop command to the connection handler
            }
        });
        primaryStage.show();
    }







    public static void main(String[] args) {
        launch(args);
    }
}
