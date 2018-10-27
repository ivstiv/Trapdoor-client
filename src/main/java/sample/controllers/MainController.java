package sample.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sample.misc.RichText;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.TimeZone;

public class MainController implements Initializable {

    private Stage stage;
///    @FXML private VBox userList;
    @FXML private ListView<TextFlow> chat;
    @FXML private Button closeBtn, minimiseBtn, settingsBtn;
    @FXML private HBox topBar;
    @FXML private TextFlow bashrc;
    @FXML private TextArea chatInput;


    public MainController(Stage stage) {
        this.stage = stage;
    }

    public void initialize(URL location, ResourceBundle resources) {

        //Test dummy functions
        setBashrc();
        joinMsg("SKDown");
        //addMsg("1Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas turpis nulla, molestie eget posuere in, semper vitae orci. In vitae sapien lectus. Sed varius arcu est. Nulla consequat neque vel tempor vehicula. Ut pellentesque quam id urna finibus sollicitudin vel quis magna. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Mauris semper pulvinar eros. Vestibulum vitae ex tellus. Cras sollicitudin nunc ut mauris finibus tincidunt. Integer fermentum orci ex, in semper nisi pretium at. Vestibulum et aliquet justo, vel viverra magna. Nam sed maximus elit. Integer sollicitudin erat purus, ut fringilla lectus ultricies non. Praesent fringilla non sapien sit amet volutpat. Fusce ornare facilisis sagittis.");
        //addMsg("2Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas turpis nulla, molestie eget posuere in, semper vitae orci. In vitae sapien lectus. Sed varius arcu est. Nulla consequat neque vel tempor vehicula. Ut pellentesque quam id urna finibus sollicitudin vel quis magna. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Mauris semper pulvinar eros. Vestibulum vitae ex tellus. Cras sollicitudin nunc ut mauris finibus tincidunt. Integer fermentum orci ex, in semper nisi pretium at. Vestibulum et aliquet justo, vel viverra magna. Nam sed maximus elit. Integer sollicitudin erat purus, ut fringilla lectus ultricies non. Praesent fringilla non sapien sit amet volutpat. Fusce ornare facilisis sagittis.");

        chatInput.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    if (event.isShiftDown()) { // if the shift is down add new line. Do not send!
                        chatInput.appendText("\n");
                        return;
                    }

                    String text = chatInput.getText().trim();
                    if (!text.isEmpty()) {
                        event.consume();
                        addMsg(text);
                        chatInput.setText("");
                        chat.scrollTo(chat.getItems().size());
                    }
                }
            }
        });
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
        Date date = new Date(System.currentTimeMillis());
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        String dateFormatted = formatter.format(date);

        Text line1 = new Text("\u2554\u2550[");
        Text time = new Text(dateFormatted);
        Text line2 = new Text("]\u2550[");
        Text nick = new Text("SKDown");
        Text line3 = new Text("]\n");
        Text line4 = new Text("\u255a>");
        RichText msg = new RichText("&x"+message);

        line1.setFill(Color.AQUA);
        line2.setFill(Color.AQUA);
        line3.setFill(Color.AQUA);
        line4.setFill(Color.AQUA);
        time.setFill(Color.GRAY);
        nick.setFill(Color.WHITESMOKE);

        line1.setFont(Font.font("",FontWeight.BOLD,20));
        line2.setFont(Font.font("",FontWeight.BOLD,20));
        line3.setFont(Font.font("",FontWeight.BOLD,20));
        line4.setFont(Font.font("Consolas",FontWeight.BOLD,25.1));
        time.setFont(Font.font("Consolas",FontWeight.BOLD,20));
        nick.setFont(Font.font("Consolas",FontWeight.BOLD,20));
        msg.setCustomSize(19);
        msg.setCustomFont("Consolas");

        TextFlow flow = new TextFlow();
        // https://bugs.openjdk.java.net/browse/JDK-8089029
        // TODO: 28-Oct-18 be aware of this bug
        flow.setMaxWidth(1230);
        flow.getChildren().addAll(line1,time,line2,nick,line3,line4);
        for(Text t : msg.translateCodes())
            flow.getChildren().add(t);
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

        //RichText bsh = new RichText("&aSKDown@&b164.132.56.199:~/global $");
        //RichText bsh = new RichText("&a&1Red&2&b&aBl&bue&rRESET&aRED");
        RichText bsh = new RichText("&1&gSKDown&l@&d164.132.56.199&l:&c~/global &l$");
        bsh.setCustomSize(20);
        bsh.setCustomFont("Consolas");
        for(Text t : bsh.translateCodes())
            bashrc.getChildren().add(t);

/*

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
*/
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