package a22.sim203.tp3;

import a22.sim203.tp3.simulation.Equation;
import a22.sim203.tp3.simulation.Etat;
import a22.sim203.tp3.simulation.SimulationService;
import a22.sim203.tp3.simulation.Variable;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SimulationApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("simulateur.fxml"));
        primaryStage.setScene(new Scene(loader.load()));


        Etat etatTest = new Etat();
        Equation equation = new Equation("test", "f(x)=x+1");

        etatTest.addVariable(new Variable("x", 20));
        etatTest.getVariable("x").ajouteEquation(equation);

        SimulationService simulationService = new SimulationService("test");
        simulationService.setEtatActuel(etatTest);
        simulationService.setTempsEtIntervalTheorique(0,1);
        simulationService.start();

        simulationService.valueProperty().addListener((a,e,i) ->{
            System.out.println(i.toString());
        });

        primaryStage.show();


    }

    public static void main(String[] args) {
        launch(args);
    }
}
