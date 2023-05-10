package sample;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    public TextField strName;
    public ComboBox strHealth;
    public ToggleGroup tgr;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        strName.setText("Bunny");
        strHealth.getItems().addAll("10","50","75", "100");
        //tgr.selectToggle()
    }

    public void eventPress(ActionEvent actionEvent) {
            String name= strName.getText();

        int health=Integer.parseInt(strHealth.getValue().toString());

        RadioButton selection = (RadioButton)tgr.getSelectedToggle();
        int level=0;
        if( selection.getText().equals("Rabbit")) level=0;
        else if( selection.getText().equals("AdultRabbit")) level=1;
             else if( selection.getText().equals("BossRabbit")) level=2;

        Main.herd.addRabbit(level, name, health );

        DialogNewShip.window.close();
    }
}
