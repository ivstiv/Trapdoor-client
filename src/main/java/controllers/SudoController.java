package controllers;

import com.google.gson.JsonObject;
import communication.ServerConnection;
import core.ServiceLocator;
import data.Request;
import data.RequestType;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        cmdField.appendText(command);

        Platform.runLater(() -> passwordField.requestFocus());

        if(ServiceLocator.hasService(ServerConnection.class)) {
            ServerConnection conn = ServiceLocator.getService(ServerConnection.class);

            titleLabel.setText(titleLabel.getText()+conn.getUSERNAME()+":");
        }

        executeBtn.setOnAction((event) -> {

            if(passwordField.getText().trim().isEmpty()) return;

            if(ServiceLocator.hasService(ServerConnection.class)) {
                ServerConnection conn = ServiceLocator.getService(ServerConnection.class);

                // send action for password confirmation
                JsonObject payload = new JsonObject();
                payload.addProperty("action", "confirm_sudo");
                payload.addProperty("session_id", sessionId);
                payload.addProperty("sudo_password", passwordField.getText().trim());
                Request confirmationReq = new Request(RequestType.ACTION, payload);
                conn.sendRequest(confirmationReq);
            }
            stage.close();
        });

        // submit with enter
        pane.setOnKeyPressed((event) -> {
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
