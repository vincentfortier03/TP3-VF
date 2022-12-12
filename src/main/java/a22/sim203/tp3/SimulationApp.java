package a22.sim203.tp3;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Classe de l'application SimulationApp
 *  @author Vincent Fortier
 */
public class SimulationApp extends Application {
    /**
     * permet d'obtenir un objet de type Load simplement qu'en passant le nom du fichier FXML en argument
     * @param fxmlFileName le nom du fichier FXML
     * @return un objet de type Load
     * @throws IOException une exception lancée par la méthode load
     */
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

    /**
     * Méthode qui load le fichier FXML de l'application Simulation et l'affiche
     * @param primaryStage the primary stage for this application, onto which
     *                     the application scene can be set.
     *                     Applications may create other stages, if needed, but they will not be
     *                     primary stages.
     * @throws Exception une excpetion lancée par la méthode loadFXML
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setScene(new Scene(loadFXML("Calculatrice.fxml").getRoot()));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Interface de l'objet Load
     */
    public interface Load {
        /**
         * retourne le controller de l'objet load
         * @return le controller
         */
        public Object getController();

        /**
         * retourne la racine de l'objet load
         * @return la racine
         */
        public Parent getRoot();
    }

}



