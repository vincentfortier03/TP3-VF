package a22.sim203.tp3;

import a22.sim203.tp3.simulation.Equation;
import a22.sim203.tp3.simulation.Etat;
import a22.sim203.tp3.simulation.Simulation;
import a22.sim203.tp3.simulation.Variable;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DialoguesUtils {
//todo

    public static void saveFileDialog(Window ownerWindow, Simulation simulationASave) throws IOException {
        FileChooser fileChooser = new FileChooser();
        File fichierDeSauvegarde = fileChooser.showSaveDialog(ownerWindow);
        File fichierInitial = new File(System.getProperty("user.home"));
        ObjectOutputStream bw = null;

        fileChooser.setTitle("Choisir l'emplacement pour le fichier de sauvegarde");
        fileChooser.setInitialDirectory(fichierInitial);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichier de simulation", "*.sim"));

        try {
            bw = new ObjectOutputStream(new FileOutputStream(fichierDeSauvegarde));
            bw.writeObject(simulationASave);
        } catch (Exception e) {
            System.out.println("failed");
        }finally {
            bw.close();
        }
    }

    public static Simulation openFileDialog(Window ownerWindow) throws IOException {
        Simulation simulationChargee = null;
        FileChooser fileChooser = new FileChooser();
        File fichierInitial = new File(System.getProperty("user.dir"));
        File fichierACharger = null;

        fileChooser.setTitle("Choisissez le fichier de simulation à charger");
        fileChooser.setInitialDirectory(fichierInitial);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichier de simulation", "*.sim"));

        fichierACharger = fileChooser.showOpenDialog(ownerWindow);

        if(fichierACharger != null){
            ObjectInputStream bw = new ObjectInputStream(new FileInputStream(fichierACharger));
            try {
                simulationChargee = (Simulation)bw.readObject() ;
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }finally {
                bw.close();
            }
        }

        return simulationChargee;
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

    public static Simulation dialogSimulation(boolean modification, Simulation simulation) throws IOException {
        FXMLLoader loader = new FXMLLoader(SimulationApp.class.getResource("AjouterSimulation.fxml"));
        Parent root = loader.load();

        TextField textFieldNom = (TextField) loader.getNamespace().get("textFieldNom");
        Etat etatInitial = null;
        String nom;

        Alert dialogAjouterSim = new Alert(Alert.AlertType.CONFIRMATION);
        dialogAjouterSim.getDialogPane().setContent(root);

        if (modification) {
            dialogAjouterSim.setTitle("Modifier une simulation");
            dialogAjouterSim.setHeaderText("Modifier une simulation");
            textFieldNom.setText(simulation.getName());
        } else {
            dialogAjouterSim.setTitle("Ajouter une simulation");
            dialogAjouterSim.setHeaderText("Ajouter une simulation");
            etatInitial = new Etat();
        }

        Optional<ButtonType> type = dialogAjouterSim.showAndWait();
        if (type.isPresent() && type.get() == ButtonType.OK) {
            nom = textFieldNom.getText();

            if (modification) {
                simulation.setName(nom);
            } else {
                simulation = new Simulation(nom, etatInitial);
            }
        }

        return simulation;
    }

    public static Variable dialogueVariable(boolean modifier, Variable variableSaisie) throws IOException {
        FXMLLoader loader = new FXMLLoader(SimulationApp.class.getResource("AjouterVariable.fxml"));
        Parent root = loader.load();

        String nom;
        double valeur = 0;

        TextField textFieldNom = (TextField) loader.getNamespace().get("textFieldNom");
        TextField textFieldValeur = (TextField) loader.getNamespace().get("textFieldValeur");

        Alert dialogAjouterVariable = new Alert(Alert.AlertType.CONFIRMATION);
        dialogAjouterVariable.getDialogPane().setContent(root);

        if (modifier) {
            dialogAjouterVariable.setTitle("Modifier une variable");
            dialogAjouterVariable.setHeaderText("Modifier une variable");
            textFieldNom.setText(variableSaisie.getName());
            textFieldValeur.setText("" + variableSaisie.getValue());

        } else {
            dialogAjouterVariable.setTitle("Ajouter une variable");
            dialogAjouterVariable.setHeaderText("Ajouter une variable");
        }

        Optional<ButtonType> type = dialogAjouterVariable.showAndWait();
        if (type.isPresent() && type.get() == ButtonType.OK) {
            nom = textFieldNom.getText();
            valeur = Double.parseDouble(textFieldValeur.getText());

            if (modifier) {
                variableSaisie.setName(nom);
                variableSaisie.setValue(valeur);

            } else {
                variableSaisie = new Variable(nom, valeur);
            }
        }
        return variableSaisie;
    }

    public static Equation dialogueEquation(Equation equation) throws IOException {
        FXMLLoader loader = new FXMLLoader(SimulationApp.class.getResource("AjouterEquation.fxml"));
        Parent root = loader.load();

        String nom;
        String equationSaisie = "";

        TextField textFieldNom = (TextField) loader.getNamespace().get("nomTextField");
        TextField textFieldEquation = (TextField) loader.getNamespace().get("equationTextField");

        Alert dialogAjouterEquation = new Alert(Alert.AlertType.CONFIRMATION);
        dialogAjouterEquation.getDialogPane().setContent(root);

        if (equation != null) {
            dialogAjouterEquation.setTitle("Modifier une équation");
            dialogAjouterEquation.setHeaderText("Modifier une équation");
            textFieldNom.setText(equation.getName());
            textFieldEquation.setText("" + equation.getExpression());
        } else {
            dialogAjouterEquation.setTitle("Ajouter une équation");
            dialogAjouterEquation.setHeaderText("Ajouter une équation");
        }

        Optional<ButtonType> type = dialogAjouterEquation.showAndWait();


        if (type.isPresent() && type.get() == ButtonType.OK) {
            nom = textFieldNom.getText();
            equationSaisie = textFieldEquation.getText();

            if (equation != null) {
                equation.setName(nom);
                equation.setExpression(equationSaisie);

            } else {
                equation = new Equation(nom, equationSaisie);
            }
        }
        return equation;
    }


}
