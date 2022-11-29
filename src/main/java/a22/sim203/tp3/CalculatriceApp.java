package TP2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


/**
 * Classe calculatrice app. Classe démarrant l'application
 *
 * @author Vincent Fortier
 */
public class CalculatriceApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("fxml/Calculatrice.fxml"));
        Pane root = loader.load();


        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}
