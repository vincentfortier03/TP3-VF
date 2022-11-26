package a22.sim203.tp3;

import a22.sim203.tp3.controlleurs.SimulateurController;
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



        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
