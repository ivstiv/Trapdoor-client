package controllers;

import com.google.gson.JsonObject;
import communication.ServerConnection;
import core.ServiceLocator;
import data.Request;
import data.RequestType;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class SudoController implements Initializable {

    @FXML private Button executeBtn;
    @FXML private TextField cmdField, passwordField;
    @FXML private Label titleLabel;
    @FXML private GridPane pane;
    private String sessionId, command;
    private Stage stage;


    /*
        executeBtn.fire() causes the listener to fire 2 times which didn't make any
        sense to me so this is why this variable exists. To prevent double sending..
        // TODO: 10-Mar-19 review this bug in the future!
     */
    private boolean triggered;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        cmdField.appendText(command);

        Platform.runLater(() -> passwordField.requestFocus());

        if(ServiceLocator.hasSerivce(ServerConnection.class)) {
            ServerConnection conn = ServiceLocator.getService(ServerConnection.class);

            titleLabel.setText(titleLabel.getText()+conn.getUSERNAME()+":");
        }

        executeBtn.setOnAction((event) -> {

            if(passwordField.getText().trim().isEmpty()) return;
            if(triggered) return; // check the comment on the top to see why this is here :D

            if(ServiceLocator.hasSerivce(ServerConnection.class)) {
                ServerConnection conn = ServiceLocator.getService(ServerConnection.class);

                // send action for password confirmation
                JsonObject payload = new JsonObject();
                payload.addProperty("action", "confirm_sudo");
                payload.addProperty("session_id", sessionId);
                payload.addProperty("sudo_password", passwordField.getText().trim());
                Request confirmationReq = new Request(RequestType.ACTION, payload);
                conn.sendRequest(confirmationReq);
                triggered = true;
            }
            stage.close();
        });

        // submit with enter
        passwordField.setOnKeyPressed((event) -> {
            if (event.getCode() == KeyCode.ENTER) {
                event.consume();
                executeBtn.fire();
            }
        });

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

    public SudoController(Stage stage, String command, String sessionId) {
        this.command = command;
        this.sessionId = sessionId;
        this.stage = stage;
    }



}
