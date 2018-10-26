package sample;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import sample.communication.ConnectionHandler;
import sample.controllers.MainController;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        final MainController mainController = new MainController(primaryStage);

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/fxml/mainScreen.fxml"));
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
