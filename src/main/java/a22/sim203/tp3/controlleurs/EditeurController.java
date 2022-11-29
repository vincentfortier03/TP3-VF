package a22.sim203.tp3.controlleurs;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

public class EditeurController implements Initializable {

    @FXML
    private ListView<?> listViewEditeur;

    @FXML
    private ListView<?> listViewEquations;

    @FXML
    private ListView<?> listViewVariables;

    @FXML
    private BorderPane root;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
