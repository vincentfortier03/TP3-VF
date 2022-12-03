package a22.sim203.tp3;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SimulationApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setScene(new Scene(loadFXML("Calculatrice.fxml").getRoot()));
        primaryStage.show();

        Stage simStage = new Stage();
        simStage.setScene(new Scene(loadFXML("Simulateur.fxml").getRoot()));
        simStage.show();

    }

    public static Load loadFXML(String fxmlFileName) throws IOException {
        FXMLLoader loader = new FXMLLoader(SimulationApp.class.getResource(fxmlFileName));
        Parent root = loader.load();
        Object controller = loader.getController();

        return new Load(){

            @Override
            public Object getController() {
                return controller;
            }

            @Override
            public Parent getRoot() {
                return root;
            }
        };

    }

    public static void main(String[] args) {
        launch(args);
    }

    public interface Load {
        public Object getController();

        public Parent getRoot();
    }

}



