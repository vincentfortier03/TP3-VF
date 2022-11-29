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
import javafx.stage.Stage;

import java.io.IOException;
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

    public Etat etatTest;

    public SimulationService simService;


    public Equation equation;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        equation = new Equation("test", "f(x)=x+1");
        etatTest = new Etat();
        etatTest.addVariable(new Variable("x", 20));
        etatTest.getVariable("x").ajouteEquation(equation);


        simService = new SimulationService("test", etatTest);
        simService.setTempsEtIntervalTheorique(0,1);

        try{
            simService.save((Stage)(SimulationApp.loadFXML("Simulateur.fxml").getRoot()));
        }catch (Exception e){
            System.out.println(e.getMessage());
        }


    }

    @FXML
    public void simStart(ActionEvent event){


        simService.setOnSucceeded((i) ->{
            System.out.println("complété");
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
}