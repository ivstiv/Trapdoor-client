package controllers;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
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
    @FXML private GridPane pane;
    private String sessionId, command;
    private Stage stage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        cmdField.appendText(command);

        executeBtn.setOnAction(event -> {
            System.out.println(sessionId);
            stage.close();
        });

        // submit with enter
        cmdField.setOnKeyPressed((event) -> {
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
