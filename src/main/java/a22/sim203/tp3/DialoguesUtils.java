package a22.sim203.tp3;

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


       File fichierDeSauvegarde = fileChooser.showOpenDialog(ownerWindow);
       return fichierDeSauvegarde;
    }


}
