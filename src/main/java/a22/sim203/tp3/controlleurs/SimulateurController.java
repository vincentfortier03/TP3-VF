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
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
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

    private SimulationService simService;

    private Simulation simulation;

    private int t;

    private int dtTheorique;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setTDT(0,1);
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
            System.out.println(simService.getValue());
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

    @FXML
    void saveToCustomFile(ActionEvent event) {

    }

    public void setTDT(int t, int dtTheorique){
        this.t = t;
        this.dtTheorique = dtTheorique;

    }
}