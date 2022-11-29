package a22.sim203.tp3.controlleurs;

import a22.sim203.tp3.simulation.Equation;
import a22.sim203.tp3.simulation.Etat;
import a22.sim203.tp3.simulation.Simulation;
import a22.sim203.tp3.simulation.Variable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

public class EditeurController implements Initializable {

    @FXML
    private ListView<Simulation> listViewSimulations;

    @FXML
    private ListView<Equation> listViewEquations;

    @FXML
    private ListView<Variable> listViewVariables;

    @FXML
    private BorderPane root;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        listViewEquations = new ListView<>();
        listViewSimulations = new ListView<>();
        listViewSimulations = new ListView<>();



    }


    @FXML
    public void ajouterSimulation(ActionEvent actionEvent) {
        listViewSimulations.getItems().add(new Simulation("Name", new Etat()));
        System.out.println("ajoute");
    }

    @FXML
    public void charger(ActionEvent actionEvent) {

    }

    @FXML
    public void save(ActionEvent actionEvent) {

    }
}
