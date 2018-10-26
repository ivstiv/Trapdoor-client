package sample.controllers;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sample.communication.ConnectionHandler;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    private Stage stage;
///    @FXML private VBox userList;
    @FXML private ListView<TextFlow> chat;
    @FXML private Button closeBtn, minimiseBtn, settingsBtn;
    @FXML private HBox topBar;
    @FXML private TextFlow bashrc;


    public MainController(Stage stage) {
        this.stage = stage;
    }

    public void initialize(URL location, ResourceBundle resources) {

        //Test dummy functions
        setBashrc();
        joinMsg("SKDown");
        addMsg("1Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas turpis nulla, molestie eget posuere in, semper vitae orci. In vitae sapien lectus. Sed varius arcu est. Nulla consequat neque vel tempor vehicula. Ut pellentesque quam id urna finibus sollicitudin vel quis magna. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Mauris semper pulvinar eros. Vestibulum vitae ex tellus. Cras sollicitudin nunc ut mauris finibus tincidunt. Integer fermentum orci ex, in semper nisi pretium at. Vestibulum et aliquet justo, vel viverra magna. Nam sed maximus elit. Integer sollicitudin erat purus, ut fringilla lectus ultricies non. Praesent fringilla non sapien sit amet volutpat. Fusce ornare facilisis sagittis.");
        addMsg("2Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas turpis nulla, molestie eget posuere in, semper vitae orci. In vitae sapien lectus. Sed varius arcu est. Nulla consequat neque vel tempor vehicula. Ut pellentesque quam id urna finibus sollicitudin vel quis magna. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Mauris semper pulvinar eros. Vestibulum vitae ex tellus. Cras sollicitudin nunc ut mauris finibus tincidunt. Integer fermentum orci ex, in semper nisi pretium at. Vestibulum et aliquet justo, vel viverra magna. Nam sed maximus elit. Integer sollicitudin erat purus, ut fringilla lectus ultricies non. Praesent fringilla non sapien sit amet volutpat. Fusce ornare facilisis sagittis.");
        addMsg("3Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas turpis nulla, molestie eget posuere in, semper vitae orci. In vitae sapien lectus. Sed varius arcu est. Nulla consequat neque vel tempor vehicula. Ut pellentesque quam id urna finibus sollicitudin vel quis magna. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Mauris semper pulvinar eros. Vestibulum vitae ex tellus. Cras sollicitudin nunc ut mauris finibus tincidunt. Integer fermentum orci ex, in semper nisi pretium at. Vestibulum et aliquet justo, vel viverra magna. Nam sed maximus elit. Integer sollicitudin erat purus, ut fringilla lectus ultricies non. Praesent fringilla non sapien sit amet volutpat. Fusce ornare facilisis sagittis.");

        closeBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Platform.exit();
            }
        });

        minimiseBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                stage.setIconified(true);
            }
        });

        // Make the window draggable
        Offset offset = new Offset();
        topBar.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                offset.x = stage.getX() - event.getScreenX();
                offset.y = stage.getY() - event.getScreenY();
            }
        });
        topBar.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                stage.setX(event.getScreenX() + offset.x);
                stage.setY(event.getScreenY() + offset.y);
                stage.getScene().setCursor(Cursor.CLOSED_HAND);
            }
        });
        topBar.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                stage.getScene().setCursor(Cursor.DEFAULT);
            }
        });
    }

    public void stop() {
        // sending a message to the connection handler to stop the connection
    }

    public void addMsg(String message) {
        Text line1 = new Text("\u2554\u2550[");
        Text time = new Text("01:00:26");
        Text line2 = new Text("]\u2550[");
        Text nick = new Text("SKDown");
        Text line3 = new Text("]\n");
        Text line4 = new Text("\u255a>");
        Text msg = new Text(message);

        line1.setFill(Color.AQUA);
        line2.setFill(Color.AQUA);
        line3.setFill(Color.AQUA);
        line4.setFill(Color.AQUA);
        time.setFill(Color.GRAY);
        nick.setFill(Color.WHITESMOKE);
        msg.setFill(Color.WHITESMOKE);

        line1.setFont(Font.font("",FontWeight.BOLD,20));
        line2.setFont(Font.font("",FontWeight.BOLD,20));
        line3.setFont(Font.font("",FontWeight.BOLD,20));
        line4.setFont(Font.font("Consolas",FontWeight.BOLD,25.1));
        time.setFont(Font.font("Consolas",FontWeight.BOLD,20));
        nick.setFont(Font.font("Consolas",FontWeight.BOLD,20));
        msg.setFont(Font.font("Consolas",18));

        TextFlow flow = new TextFlow();
        flow.setMaxWidth(1000);
        flow.getChildren().addAll(line1,time,line2,nick,line3,line4,msg);
        chat.getItems().add(flow);
    }

    public void joinMsg(String nickname) {
        Text t1 = new Text("[");
        t1.setFill(Color.AQUA);
        Text t2 = new Text("Server");
        t2.setFill(Color.AQUA);
        Text t3 = new Text("] ");
        t3.setFill(Color.AQUA);
        Text t4 = new Text(nickname+" has joined!");
        t4.setFill(Color.GOLDENROD);

        t1.setFont(Font.font("",FontWeight.BOLD,20));
        t3.setFont(Font.font("",FontWeight.BOLD,20));
        t2.setFont(Font.font("Consolas",FontWeight.BOLD,20));
        t4.setFont(Font.font("Consolas",FontWeight.BOLD,20));
        TextFlow flow = new TextFlow();
        flow.setMaxWidth(1000);
        flow.getChildren().addAll(t1,t2,t3,t4);
        chat.getItems().add(flow);
    }

    public void setBashrc() {

        Text nick = new Text("SKDown");
        Text at = new Text("@");
        Text ip = new Text("164.132.56.199");
        Text dots = new Text(":");
        Text channel = new Text("~/global ");
        Text bash = new Text("$");

        nick.setFill(Color.GOLDENROD);
        at.setFill(Color.LIGHTGRAY);
        ip.setFill(Color.CYAN);
        dots.setFill(Color.LIGHTGRAY);
        channel.setFill(Color.CADETBLUE);
        bash.setFill(Color.LIGHTGRAY);

        nick.setFont(Font.font("Consolas",FontWeight.BOLD,20));
        at.setFont(Font.font("Consolas",FontWeight.BOLD,20));
        ip.setFont(Font.font("Consolas",FontWeight.BOLD,20));
        dots.setFont(Font.font("Consolas",FontWeight.BOLD,20));
        channel.setFont(Font.font("Consolas",FontWeight.BOLD,20));
        bash.setFont(Font.font("Consolas",FontWeight.BOLD,20));

        bashrc.getChildren().addAll(nick,at,ip,dots,channel,bash);
    }

    public void openConnectWindow() {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/connect.fxml"));
        loader.setController(new ConnectController(stage));
        Scene scene = null;
        try {
            scene = new Scene((Parent) loader.load(), 400, 600);
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.show();
    }
}