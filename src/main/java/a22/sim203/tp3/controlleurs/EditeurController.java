package a22.sim203.tp3.controlleurs;

import a22.sim203.tp3.simulation.*;
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

    private Simulation currentSim;

    public Etat etatTest;

    public SimulationService simService;

    public Equation equation;

    public Etat lastEtat;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        equation = new Equation("test", "f(x)=x+1");
        etatTest = new Etat();
        etatTest.addVariable(new Variable("x", 20));
        etatTest.getVariable("x").ajouteEquation(equation);


        simService = new SimulationService("test", etatTest);
        simService.setTempsEtIntervalTheorique(0,1);
        listViewEquations = new ListView<>();
        listViewSimulations = new ListView<>();
        listViewSimulations = new ListView<>();

        listViewSimulations.getItems().add(simService.getSimulation());


        listViewSimulations.selectionModelProperty().addListener((i) -> {
            listViewVariables.getItems().addAll(listViewSimulations.getSelectionModel().getSelectedItem().getHistorique(listViewSimulations.getSelectionModel().getSelectedItem().getHistorique().size()-1).getVariableList());
        });

        listViewVariables.selectionModelProperty().addListener((o) -> {
            listViewEquations.getItems().addAll(listViewVariables.getSelectionModel().getSelectedItem().getEquationsCollection());
        });



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
