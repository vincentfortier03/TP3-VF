package a22.sim203.tp3.controlleurs;

import a22.sim203.tp3.SimulationApp;
import a22.sim203.tp3.simulation.*;
import javafx.application.Application;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.chart.LineChart;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SimulateurController implements Initializable {

    @FXML
    private BorderPane root;

    @FXML
    private Button bouttonArreter;

    @FXML
    private Button bouttonDemarrer;

    @FXML
    private Button bouttonModifier;

    @FXML
    private TextField testFieldTemps;

    @FXML
    private ListView<Variable> listViewVariablesSimulateur;

    private SimulationService simService;

    private Simulation simulation;

    private Equation equation;

    private int t;

    private int dtTheorique;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setTDT(0,1);

        listViewVariablesSimulateur = new ListView<>();
        listViewVariablesSimulateur.setCellFactory((e) -> new varCell());

    }

    @FXML
    public void simStart(ActionEvent event){

        simService = new SimulationService("FE", new Etat(simulation.getLastEtat()));
        simService.setTempsEtIntervalTheorique(this.t,this.dtTheorique);

        simService.setOnSucceeded((i) ->{
            System.out.println("complété");

            simService.setStop();
        });

        simService.valueProperty().addListener((a,o,n) -> {
            listViewVariablesSimulateur.getItems().clear();
            listViewVariablesSimulateur.getItems().addAll(n.getVariableList());
            listViewVariablesSimulateur.refresh();
            System.out.println(n.getVariableList());

        });

        simService.start();

    }

    @FXML
    public void simStop(ActionEvent event){
        if (simService != null && simService.isRunning() && simService.getState() != Worker.State.CANCELLED){
            simService.setStop();
        }
    }

    public void setSimulation(Simulation simulation){
        this.simulation = simulation;
    }

    public void setEquation(Equation equation){
        this.equation = equation;
    }


    @FXML
    void saveToCustomFile(ActionEvent event) {

    }

    public void setTDT(int t, int dtTheorique){
        this.t = t;
        this.dtTheorique = dtTheorique;

    }

    private void refreshVariables(){

    }

    static class varCell extends ListCell<Variable> {
        private String stringAAfficher;
        private HBox hBox;
        private Label label;

        public varCell(){
            label = new Label();
            hBox = new HBox(label);
        }

        @Override
        public void updateItem(Variable variable, boolean empty){
            super.updateItem(variable,empty);

            if(variable == null || empty){
                setItem(new Variable("null", 0));
                label.setText("null");
                setGraphic(hBox);
            }else{
                stringAAfficher = variable.getName();
                setItem(variable);
                label.setText(stringAAfficher + " = " + variable.getValue());
                setGraphic(hBox);
            }
        }
    }

}