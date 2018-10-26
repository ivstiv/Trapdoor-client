package sample.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ConnectController implements Initializable {


    @FXML private Button cancelBtn;
    @FXML private GridPane pane;
    private Stage stage;

    public ConnectController(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cancelBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                stage.close();
            }
        });

        // Make the window draggable
        Offset offset = new Offset();
        pane.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                offset.x = stage.getX() - event.getScreenX();
                offset.y = stage.getY() - event.getScreenY();
            }
        });
        pane.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                stage.setX(event.getScreenX() + offset.x);
                stage.setY(event.getScreenY() + offset.y);
                stage.getScene().setCursor(Cursor.CLOSED_HAND);
            }
        });
        pane.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                stage.getScene().setCursor(Cursor.DEFAULT);
            }
        });
    }
}
