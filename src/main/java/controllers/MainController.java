package controllers;

import com.google.gson.JsonObject;
import communication.ServerConnection;
import core.ServiceLocator;
import data.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import misc.RichText;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainController implements Initializable {

    private Stage stage;
    @FXML private ListView<TextFlow> chat;
    @FXML private Button closeBtn, minimiseBtn, settingsBtn;
    @FXML public MenuButton connectBtn;
    @FXML private HBox topBar;
    @FXML private TextFlow bashrc;
    @FXML private TextArea chatInput;

    private LinkedList<String> inputHistory = new LinkedList<>();
    private int historyPointer = 0;
    private final int HISTORY_LENGTH = 16;

    public MainController(Stage stage) {
        this.stage = stage;
    }

    public void initialize(URL location, ResourceBundle resources) {

        settingsBtn.setOnMouseClicked(event -> {
            //openSudoWindow("/broadcast all Helllooooooo :D", "s97HF8e3y2e");
            openSettingsWindow();
        });

        MenuItem item = new MenuItem("New connection");
        item.setOnAction(event -> {
            MenuItem i = (MenuItem) event.getSource();
            openConnectWindow(i.getText());
        });
        connectBtn.getItems().add(item);

        DataLoader dl = ServiceLocator.getService(DataLoader.class);
        for(SavedConnection e : dl.getSavedConnections().values()) {
            MenuItem i = new MenuItem(e.getIp());
            i.setOnAction(event -> {
                MenuItem click = (MenuItem) event.getSource();
                openConnectWindow(click.getText());
            });
            connectBtn.getItems().add(i);
        }

        // set default status bar
        setStatusBar(Config.getString("username"),"192.168.0.1","channel");

        Tooltip tp = new Tooltip(dl.getMessage("tooltip-status-bar"));
        tp.setFont(Font.font("Consolas",FontWeight.BOLD,15));
        Tooltip.install(bashrc, tp);

        /* CHAT INPUT */
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
                        chatInput.setText("");
                        chat.scrollTo(chat.getItems().size());
                        if(ServiceLocator.hasService(ServerConnection.class)) {

                            // add the message to the history
                            inputHistory.addFirst(text);
                            historyPointer = 0;
                            if(inputHistory.size() > HISTORY_LENGTH)
                                inputHistory.removeLast();

                            // send the message
                            JsonObject payload = new JsonObject();
                            payload.addProperty("message", text);
                            Request r = new Request(RequestType.MSG, payload);
                            ServiceLocator.getService(ServerConnection.class).sendRequest(r);
                        }
                    }
                }else if(event.getCode() == KeyCode.UP) {
                    chatInput.setText(inputHistory.get(historyPointer));
                    if(historyPointer < HISTORY_LENGTH && historyPointer < inputHistory.size()-1)
                        historyPointer++;
                }else if(event.getCode() == KeyCode.DOWN) {
                    if (historyPointer > 0)
                        historyPointer--;
                    chatInput.setText(inputHistory.get(historyPointer));
                }
            }
        });
        closeBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    if(ServiceLocator.hasService(ServerConnection.class)) {
                        ServerConnection con = ServiceLocator.getInitialisedService(ServerConnection.class);
                        con.close(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
    public void clearChat() { Platform.runLater(() -> chat.getItems().clear()); }

    public void addPrivateMsg(String sender, String receiver, String message) {
        Platform.runLater(() -> {
            Date date = new Date(System.currentTimeMillis());
            DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
            formatter.setTimeZone(TimeZone.getTimeZone(Config.getString("timezone")));
            String dateFormatted = formatter.format(date);

            Text line1 = new Text("\u2554\u2550[");
            Text time = new Text(dateFormatted);
            Text line2 = new Text("]\u2550[");
            Text nick = new Text(receiver+" <- "+sender);
            Text line3 = new Text("]\u2550[");
            Text type = new Text("private");
            Text line5 = new Text("]\n");
            Text line4 = new Text("\u255a>");
            RichText msg = new RichText("~x"+message);

            line1.setFill(Color.AQUA);
            line2.setFill(Color.AQUA);
            line3.setFill(Color.AQUA);
            line4.setFill(Color.AQUA);
            line5.setFill(Color.AQUA);
            time.setFill(Color.GRAY);
            nick.setFill(Color.WHITESMOKE);
            type.setFill(Color.PLUM);

            line1.setFont(Font.font("",FontWeight.BOLD,20));
            line2.setFont(Font.font("",FontWeight.BOLD,20));
            line3.setFont(Font.font("",FontWeight.BOLD,20));
            line5.setFont(Font.font("",FontWeight.BOLD,20));
            line4.setFont(Font.font("Consolas",FontWeight.BOLD,25.1));
            time.setFont(Font.font("Consolas",FontWeight.BOLD,20));
            nick.setFont(Font.font("Consolas",FontWeight.BOLD,20));
            type.setFont(Font.font("Consolas",FontWeight.BOLD,20));
            msg.setCustomSize(19);
            msg.setCustomFont("Consolas");

            TextFlow flow = new TextFlow();
            // https://bugs.openjdk.java.net/browse/JDK-8089029
            // TODO: 28-Oct-18 be aware of this bug
            flow.setMaxWidth(1230);
            flow.getChildren().addAll(line1,time,line2,nick,line3,type, line5, line4);
            for(Node t : msg.translateCodes())
                flow.getChildren().add(t);

            // copy message to clipboard with right click
            flow.setOnMouseClicked(event -> {
                if(event.getButton() == MouseButton.SECONDARY) {
                    // we cant copy textflows because they don't have text property
                    // the message element is a textflow consisting of multiple Text objects which we can copy
                    if(event.getTarget() instanceof TextFlow) return;

                    Text pickedItem = (Text) event.getTarget();
                    String textToCopy = pickedItem.getText();
                    Clipboard clipboard = Clipboard.getSystemClipboard();
                    ClipboardContent content = new ClipboardContent();
                    content.putString(textToCopy);
                    clipboard.setContent(content);
                }
            });

            Tooltip copyToolTip = new Tooltip("Right click to copy.");
            copyToolTip.setFont(Font.font("Consolas",FontWeight.BOLD,15));
            Tooltip.install(flow, copyToolTip);
            chat.getItems().add(flow);

            chat.scrollTo(chat.getItems().size()-1);
        });
    }

    public void addPublicMsg(String username, String message) {
        Platform.runLater(() -> {
            Date date = new Date(System.currentTimeMillis());
            DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
            formatter.setTimeZone(TimeZone.getTimeZone(Config.getString("timezone")));
            String dateFormatted = formatter.format(date);

            Text line1 = new Text("\u2554\u2550[");
            Text time = new Text(dateFormatted);
            Text line2 = new Text("]\u2550[");
            Text nick = new Text(username);
            Text line3 = new Text("]\n");
            Text line4 = new Text("\u255a>");
            RichText msg = new RichText("~x"+message);

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
            for(Node t : msg.translateCodes()) {
                flow.getChildren().add(t);
            }

            // copy message to clipboard with right click
            flow.setOnMouseClicked(event -> {
                if(event.getButton() == MouseButton.SECONDARY) {
                    // we cant copy textflows because they don't have text property
                    // the message element is a textflow consisting of multiple Text objects which we can copy
                    if(event.getTarget() instanceof TextFlow) return;

                    Text pickedItem = (Text) event.getTarget();
                    String textToCopy = pickedItem.getText();
                    Clipboard clipboard = Clipboard.getSystemClipboard();
                    ClipboardContent content = new ClipboardContent();
                    content.putString(textToCopy);
                    clipboard.setContent(content);
                }
            });

            Tooltip copyToolTip = new Tooltip("Right click to copy.");
            copyToolTip.setFont(Font.font("Consolas",FontWeight.BOLD,15));
            Tooltip.install(flow, copyToolTip);
            chat.getItems().add(flow);

            chat.scrollTo(chat.getItems().size()-1);
        });
    }

    public void print(String msg) {
        Platform.runLater(() -> {
            RichText text = new RichText(msg);
            text.setCustomSize(20);
            text.setCustomFont("Consolas");
            TextFlow flow = new TextFlow();
            // https://bugs.openjdk.java.net/browse/JDK-8089029
            // TODO: 28-Oct-18 be aware of this bug
            flow.setMaxWidth(1230);
            for(Node t : text.translateCodes())
                flow.getChildren().add(t);
            chat.getItems().add(flow);
        });
    }

    public void setStatusBar(RichText msg) {
        if(Thread.currentThread().getName().contains("JavaFX")) {
            msg.setCustomSize(20);
            msg.setCustomFont("Consolas");
            bashrc.getChildren().clear();
            for(Node t : msg.translateCodes())
                bashrc.getChildren().add(t);
        }else{
            Platform.runLater(() -> {
                msg.setCustomSize(20);
                msg.setCustomFont("Consolas");
                bashrc.getChildren().clear();
                for(Node t : msg.translateCodes())
                    bashrc.getChildren().add(t);
            });
        }
    }


    public void setStatusBar(String username, String ip, String channel) {

        String template = Config.getString("statusbar")
                .replaceFirst("%username%", username)
                .replaceFirst("%ip%", ip)
                .replaceFirst("%channel%", channel);

        RichText msg = new RichText(template);

        if(Thread.currentThread().getName().contains("JavaFX")) {
            msg.setCustomSize(20);
            msg.setCustomFont("Consolas");
            bashrc.getChildren().clear();
            for(Node t : msg.translateCodes())
                bashrc.getChildren().add(t);
        }else{
            Platform.runLater(() -> {
                msg.setCustomSize(20);
                msg.setCustomFont("Consolas");
                bashrc.getChildren().clear();
                for(Node t : msg.translateCodes())
                    bashrc.getChildren().add(t);
            });
        }
    }
    // based on the argument of IP it will change the button from save to delete
    public void openConnectWindow(String ip) {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/connect.fxml"));
        loader.setController(new ConnectController(stage, ip, this));
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

    public void openSudoWindow(String command, String sessionId) {
        Platform.runLater(() -> {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/sudo.fxml"));
            loader.setController(new SudoController(stage, command, sessionId));
            Scene scene = null;
            try {
                scene = new Scene((Parent) loader.load(), 600, 400);
            } catch (IOException e) {
                e.printStackTrace();
            }
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(scene);
            stage.show();
        });
    }

    public void openSettingsWindow() {
        Platform.runLater(() -> {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/settings.fxml"));
            loader.setController(new SettingsController(stage, this));
            Scene scene = null;
            try {
                scene = new Scene((Parent) loader.load(), 600, 400);
            } catch (IOException e) {
                e.printStackTrace();
            }
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(scene);
            stage.show();
        });
    }
}