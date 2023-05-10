package sample;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class DialogNewShip {

    public static Stage window=null;
    public static Scene scene;

    public static void display() throws IOException {


        Parent root = FXMLLoader.load(Main.class.getResource("sample.fxml"));
        window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Створіть нового зайця");

        scene = new Scene(root, 440, 358);
        window.setScene(scene);

        window.showAndWait();

    }

}

