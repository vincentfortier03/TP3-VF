package a22.sim203.tp3.controlleurs;

import a22.sim203.tp3.SimulationApp;
import a22.sim203.tp3.simulation.*;
import javafx.application.Application;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class SimulateurController implements Initializable {

    @FXML
    private Button bouttonArreter;

    @FXML
    private Button bouttonDemarrer;

    @FXML
    private Button bouttonModifier;

    @FXML
    private TextField testFieldTemps;

    public Etat etatTest = new Etat();

    public SimulationService simService;

    public Equation equation = new Equation("test", "f(x)=x+1");



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        etatTest.addVariable(new Variable("x", 20));
        etatTest.getVariable("x").ajouteEquation(equation);

        simService = new SimulationService("strinmg name", etatTest);
        simService.setTempsEtIntervalTheorique(0,1);


    }

    @FXML
    public void simStart(ActionEvent event){


        if(simService.getState() != Worker.State.READY){
            simService.reset();
        }

        simService.valueProperty().addListener((a,o,n) -> {
            System.out.println(n);
        });

        simService.stateProperty().addListener((a,o,n) -> {
            System.out.println(n);
        });

        simService.setOnCancelled((i) -> {
            simService.resetAll();
        });

        simService.setOnSucceeded((i) -> {
            simService.setStop(false);
        });

        simService.start();

    }

    @FXML
    public void simStop(ActionEvent event){
        if (simService != null && simService.isRunning() && simService.getState() != Worker.State.CANCELLED){
            simService.setStop(true);
            simService.save();
        }
    }

}