package core;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import controllers.MainController;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

/*
        String test = "тест";
        System.out.println("Plain text: "+test);
        test.codePoints().forEach(el -> System.out.print(el+" "));
        System.out.println();
        AES2 aes = new AES2();
        String encrypted = aes.encrypt(test);
        System.out.println("Encrypted base 64 text: "+encrypted);
        System.out.println("Decrypted text: "+aes.decrypt(encrypted));
        aes.decrypt(encrypted).codePoints().forEach(el -> System.out.print(el+" "));


        "тест".codePoints().forEach(el -> System.out.print(el+" "));
        System.out.println();
        JsonObject c = new JsonObject();
        c.addProperty("message", "тест");
        c.get("message").getAsString().codePoints().forEach(el -> System.out.print(el+" "));
*/
        final MainController mainController = new MainController(primaryStage);
        ServiceLocator.initialiseService(mainController);
        ServiceLocator.initialiseService(this);

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/fxml/main.fxml"));
        fxmlLoader.setController(mainController);
        Parent root = fxmlLoader.load();
        primaryStage.setResizable(true);
        //remove window decoration
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setTitle("Trapdoor");
        primaryStage.setScene(new Scene(root, 1280, 720));
        primaryStage.show();
    }







    public static void main(String[] args) {
        launch(args);
    }
}
