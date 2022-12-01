package a22.sim203.tp3;

import a22.sim203.tp3.simulation.Simulation;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DialoguesUtils {
//todo

    public static File fileChooserDialog(Window ownerWindow){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir le fichier de sauvegarde");

        File fichierInitial = new File(System.getProperty("user.home"));
        fileChooser.setInitialDirectory(fichierInitial);

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichier de simulation", "*.sim"));


       File fichierDeSauvegarde = fileChooser.showSaveDialog(ownerWindow);
       return fichierDeSauvegarde;
    }

    public static void alerteNaN() {
        Alert alerteCalculNaN = new Alert(Alert.AlertType.WARNING);
        alerteCalculNaN.setTitle("Calculateur avancé");
        alerteCalculNaN.setHeaderText("Expression invalide");
        alerteCalculNaN.setContentText("L'expression saisie ne peut être calculée");
        alerteCalculNaN.showAndWait();
    }

    /**
     * Méthode lançant un dialogue TextInput pour demander une fonction à l'utilisateur. Permet aussi de mettre du texte
     * par défaut dans la boite de texte
     *
     * @param str le texte par défaut
     * @return un dialog de type TextInputDialog configuré
     */
    public static TextInputDialog layoutFctDialog(String str) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Saisie de fonction");
        dialog.setContentText("Saisissez la fonction: f(x)= ");
        dialog.setTitle("Calculateur avancé");
        dialog.getEditor().setText(str);
        return dialog;
    }

    /**
     * Méthode affichant un dialogue d'avertissement indiquant que le texte ne peut pas être converti en nombre
     */
    public static void alerteCalculFct() {
        Alert alerteCalculFct = new Alert(Alert.AlertType.WARNING);
        alerteCalculFct.setTitle("Calculateur avancé");
        alerteCalculFct.setHeaderText("Nombre invalide");
        alerteCalculFct.setContentText("le texte ne peut pas être converti en nombre");
        alerteCalculFct.showAndWait();
    }

    /**
     * Alerte déclanchée par le bouton À propos de la barre de menu. Affiche des informations sur le projet.
     */
    public static void alerteAide() {
        Alert alerteAide = new Alert(Alert.AlertType.INFORMATION);
        alerteAide.setTitle("Calculateur avancé");
        alerteAide.setHeaderText("Sim203");
        alerteAide.setContentText("Cours de programmation 203 en SIM \nCégep Limoilou A22\npar: Vincent Fortier");
        alerteAide.showAndWait();
    }

    public static Simulation dialogSimulation(){
        TextInputDialog dialog = new TextInputDialog();

        dialog.setHeaderText("Entrer une simulation");
        dialog.setContentText("Saisissez le nom de la simulation: ");
        dialog.setTitle("Editeur de simulation");

        dialog.getEditor().setText(str);

        return dialog;
    }


}
