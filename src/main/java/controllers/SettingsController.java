package controllers;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import communication.ServerConnection;
import core.ServiceLocator;
import data.Config;
import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.AbstractMap;
import java.util.Map;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {

    @FXML private Button cancelBtn, saveBtn;
    @FXML private TextField username, statusbar;
    @FXML private MenuButton timezone, language;
    @FXML private GridPane pane;
    private Stage stage;
    private MainController mainController;

    public SettingsController(Stage stage, MainController mainController) {
        this.stage = stage;
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // load the config file into the window
        username.setText(Config.getString("username"));
        statusbar.setText(Config.getString("statusbar"));
        timezone.setText(Config.getString("timezone"));
        language.setText(Config.getString("language"));

        cancelBtn.setOnAction(event -> stage.close());

        saveBtn.setOnAction(event -> {
            Config.setProperty("username", new JsonPrimitive(username.getText()));
            Config.setProperty("statusbar", new JsonPrimitive(statusbar.getText()));
            Config.setProperty("timezone", new JsonPrimitive(timezone.getText()));
            Config.setProperty("language", new JsonPrimitive(language.getText()));
            Config.updateFile();

            // update the status bar with the data of the connection if it exists
            if(ServiceLocator.hasService(ServerConnection.class)) {
                ServerConnection sc = ServiceLocator.getService(ServerConnection.class);
                mainController.setStatusBar(sc.getUSERNAME(), sc.getIP(), sc.getChannel());
            }else{
                mainController.setStatusBar(Config.getString("username"),"192.168.0.1","channel");
            }

            stage.close();
        });

        timezone.getItems().stream().forEach(item ->
            item.setOnAction(event -> {
                timezone.setText(item.getText());
            })
        );

        // Make the window draggable
        Offset offset = new Offset();
        pane.setOnMousePressed(event -> {
                    offset.x = stage.getX() - event.getScreenX();
                    offset.y = stage.getY() - event.getScreenY();
                }
        );
        pane.setOnMouseDragged(event -> {
                    stage.setX(event.getScreenX() + offset.x);
                    stage.setY(event.getScreenY() + offset.y);
                    stage.getScene().setCursor(Cursor.CLOSED_HAND);
                }
        );
        pane.setOnMouseReleased(event -> stage.getScene().setCursor(Cursor.DEFAULT));
    }
}
