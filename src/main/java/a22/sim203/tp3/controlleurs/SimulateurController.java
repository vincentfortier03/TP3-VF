package a22.sim203.tp3.controlleurs;

import a22.sim203.tp3.DialoguesUtils;
import a22.sim203.tp3.simulation.*;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;

/**
 * Classe controlleur du fichier FXML Simulateur.fxml
 *
 * @author Vincent Fortier
 */
public class SimulateurController implements Initializable {

    /**
     * la racine de la scène
     */
    @FXML
    private BorderPane root;

    /**
     * Le boutton appelant la méthode arretant le service du simulateur
     */
    @FXML
    private Button bouttonArreter;

    /**
     * boutton appelant la méthode lançant le service du simulateur
     */
    @FXML
    private Button bouttonDemarrer;

    /**
     * boutton appelant la méthode permettant de modifier la variable sélectionnée dans la listView des variables
     */
    @FXML
    private Button bouttonModifier;

    /**
     * le text field dans lequel est affiché le temps
     */
    @FXML
    private TextField textFieldTemps;

    /**
     * la listView affichant les varibles de la simulation
     */
    @FXML
    private ListView<Variable> listViewVariablesSimulateur;

    /**
     * le TextField ou l'utilisateur entre la valeur maximale de l'axe des X
     */
    @FXML
    private TextField maxX;
    /**
     * le TextField ou l'utilisateur entre la valeur maximale de l'axe des Y
     */
    @FXML
    private TextField maxY;

    /**
     * La CheckBox permettant de définir si le graphique est statique
     */
    @FXML
    private CheckBox rangeStatique;

    /**
     * le TextField ou l'utilisateur entre l'interval de temps à utiliser
     */
    @FXML
    private TextField textFieldInterval;

    /**
     * le graphique dans lequel on affiche la simulation des variables
     */
    private LineChart<Number, Number> lineChartSimulateur;

    /**
     * l'axe des ordonnés
     */
    private NumberAxis xAxis;

    /**
     * l'axe des absices
     */
    private NumberAxis yAxis;

    /**
     * le service actuellement en cours d'éxécution
     */
    private SimulationService serviceActuel;

    /**
     * la simulation actuellement simulée
     */
    private Simulation simulationActuelle;

    /**
     * le temps du service quand il a été mise sur pause
     */
    private float pauseTime;

    /**
     * le dernier état de la simulation du service quand il a été mise sur pause
     */
    private Etat etatAvantPause;

    /**
     * l'interval de temps du service quand il a été mise sur pause
     */
    private double dtAvantPause;

    /**
     * booléen indiquant si le service est sur pause
     */
    private boolean isPaused = false;

    /**
     * l'indice de la varible actuellement sélectionnée dans la listView des variables
     */
    private int variableIndex = 0;
    /**
     * L'équation actuellement sélectionnée dans l'éditeur d'équation
     */
    private Equation equationSelectionnee;

    /**
     * une map contenant le nom des variables (les clées) auxquelles sont associé des séries
     */
    private Map<String, XYChart.Series> seriesMap;
    /**
     * l'interval théorique de du service
     */
    private double intervalTheorique;

    //f(t,y)=(-9.8(0.1)(t^2)/2)+y
    //f(x)=x/1.5

    /**
     * Méthode initialisant certaines variables de la classe
     * @param location  The location used to resolve relative paths for the root object, or
     *                  {@code null} if the location is not known.
     * @param resources The resources used to localize the root object, or {@code null} if
     *                  the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //isntancie les axes
        xAxis = new NumberAxis();
        yAxis = new NumberAxis();
        //isntancie le graphique en lui passant les axes plus haut
        lineChartSimulateur = new LineChart<>(xAxis, yAxis);
        //ajuste certains paramètres des axes
        xAxis.setAutoRanging(false);
        yAxis.setAutoRanging(true);
        xAxis.setLowerBound(0);
        yAxis.setLowerBound(0);
        root.setCenter(lineChartSimulateur);
        //définit la CellFactory de la list view des variables
        listViewVariablesSimulateur.setCellFactory((e) -> new varCell());
        maxY.setText("");
        maxX.setText("");
        //crée la map des séries
        seriesMap = new TreeMap<>();

        //désactive les champs de texte permettant de modifier le range des axes
        setDisableBoundaryControls(true);
        //listener qui active les contrôles de modification du range des axes si la case est cochée ou le désactive
        rangeStatique.selectedProperty().addListener((e) -> {
            setDisableBoundaryControls(!rangeStatique.isSelected());
        });

        //listener qui change la série affichée dans le graphique en fonction de la variable sélectionnée dans la listeView
        listViewVariablesSimulateur.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<Variable>() {
            @Override
            public void onChanged(Change<? extends Variable> c) {
                setDisableSimulationControls(listViewVariablesSimulateur.getSelectionModel().isEmpty());
                refreshChart();
            }
        });
    }

    /**
     * permet de mettre en pause la simulation
     * @param event le clique du boutton
     */
    @FXML
    void simPause(ActionEvent event) {
        if (!serviceActuel.getStop() && serviceActuel.getState() == Worker.State.RUNNING) {
            serviceActuel.setStop();
            isPaused = true;
            //définit les informations du service qui a été mis en pause
            pauseTime = serviceActuel.getTempsActuel();
            dtAvantPause = serviceActuel.getIntervalTheorique();
            etatAvantPause = new Etat(serviceActuel.getValue());
        }
    }

    /**
     * Méthode d'allègement qui retourne l'indice de la varible sélectionnée dans la listView des variables
     * @return l'indice de la variable
     */
    public Variable getVariableIndex() {
        return listViewVariablesSimulateur.getItems().get(variableIndex);
    }

    /**
     * permet de changer la série qui est affichée dans le graphique
     */
    public void refreshChart(){
        //on désactive les animations pour éviter les conflits entre le "data" du lineChart et le "displayedData" de
        //celui-ci. Sans cette précaution, il y a une désynchronisation entre ce le data qui est
        // affiché et le data qui est réellement contenu dans le linechart
        lineChartSimulateur.setAnimated(false);
        lineChartSimulateur.getData().clear();
        lineChartSimulateur.getData().add(seriesMap.get(listViewVariablesSimulateur.getSelectionModel().getSelectedItem().getName()));
        //on réactive les animations
        lineChartSimulateur.setAnimated(true);
    }


    /**
     * méthode qui déclanche le service du simulateur
     * @param event le clique du boutton démarrer
     */
    @FXML
    public void simStart(ActionEvent event) {

        if (isPaused) { //si la simulation était en pause on reprendre les informations de l'ancien service et on en
                        //crée un nouveau
            serviceActuel = new SimulationService(simulationActuelle.getName(), etatAvantPause,pauseTime,dtAvantPause);
            isPaused = false;
        } else{ //sinon on en crée un nouveau en effaçant les anciennes données du graphique
            clearSeries();
            modifierIntervalTheorique();
            serviceActuel = new SimulationService(simulationActuelle.getName(), new Etat(simulationActuelle.getPlusRecentEtat()),0,getIntervalTheorique());
            textFieldInterval.setText(serviceActuel.getIntervalTheorique()+"");
        }

        setGraphBoundaries();  //on ajuste les limites des axes
        if (getVariableIndex() != null) {
            serviceActuel.setCurentlySimulatedVariable(getVariableIndex());

            if (!xAxis.isAutoRanging()) { //si les axes ne sont pas en automatique on passe les limites au service
                serviceActuel.setTimeBoundary(xAxis.getUpperBound());
            }

            if (!yAxis.isAutoRanging()) {
                serviceActuel.setValueBoundary(yAxis.getUpperBound());
            }
            serviceActuel.start();
        }

        serviceActuel.setOnRunning((event1 -> { //on désactive ce qui ne doit pas être modifié pendant que le service roule
            textFieldInterval.setDisable(true);
            setDisableBoundaryControls(true);
        }));

        serviceActuel.setOnSucceeded((i) -> { //on réactive ce qu'on a désactivé
            activerUI();
        });


        setupValueListener();//on appelle la méthode qui s'occupe de gérer les données du service
    }

    /**
     * permet de modifier l'interval théorique selon ce qui est inscrit dans le textBox de modification d'interval
     */
    public void modifierIntervalTheorique(){
        double intervalTheorique = 1;
        //si le text inscrit dans le textBox est un chiffre plus grand que 0 on set l'interval selon celui-ci
        if(isValidDouble(textFieldInterval.getText()) && Double.parseDouble(textFieldInterval.getText())>0){
            intervalTheorique = Double.parseDouble(textFieldInterval.getText());
        }
        this.intervalTheorique = intervalTheorique;
    }

    /**
     * retourne l'interval théorique du service
     * @return l'interval theorique
     */
    public double getIntervalTheorique(){
        return this.intervalTheorique;
    }

    /**
     * permet de vider les séries représentant les valeurs simulées de chaque variables
     */
    public void clearSeries(){
        for(int i = 0; i < seriesMap.size(); i++){
            seriesMap.get(listViewVariablesSimulateur.getItems().get(i).getName()).getData().clear();
        }
    }

    /**
     * Ajoute un écouteur sur les valeurs du service et met dans les séries si celles-ci ne dépassent
     * pas les limites définies
     */
    public void setupValueListener(){
        serviceActuel.valueProperty().addListener((a, o, n) -> {
            for(int i = 0; i<n.getVariableList().size(); i++) { //on parcour les varibles du plus récent état calculé
                if(rangeStatique.isSelected() && n.getVariableList().get(i).getValue()>yAxis.getUpperBound()){
                    simStop(new ActionEvent()); //s'il est hors borne on arrête le service
                }else {//on ajoute les nouvelles valeurs des variables dans les séries qui leur correspond
                    seriesMap.get(n.getVariableList().get(i).getName()).getData().add(new XYChart.Data<>(serviceActuel.getTempsEnSecondes(), serviceActuel.getValue().getVariable(n.getVariableList().get(i).getName()).getValue()));
                }
            }//on affiche le temps dans le textBox à cet issue
            textFieldTemps.setText("" + serviceActuel.getTempsEnSecondes());
        });
    }

    /**
     * interrompt le service et réactive l'interface graphique
     * @param event le clique du boutton arrêter
     */
    @FXML
    public void simStop(ActionEvent event) {
        if (serviceActuel != null && serviceActuel.isRunning() && serviceActuel.getState() != Worker.State.CANCELLED) {
            serviceActuel.cancel();
            activerUI();
        }
    }

    /**
     * réactive les champs de modification des axes et de l'interval
     */
    public void activerUI(){
        if(rangeStatique.isSelected()){
            maxX.setDisable(false);
            maxY.setDisable(false);
        }
        textFieldInterval.setDisable(false);
    }

    /**
     * définit l'équation simulée selon celle qui est sélectionnée dans l'éditeur d'équation
     * @param equationSelectionnee l'équation sélectionnée
     */
    public void setEquationSelectionnee(Equation equationSelectionnee) {
        this.equationSelectionnee = equationSelectionnee;
    }

    /**
     * définit la simulation qui est sélectionnée dans l'éditeur et crée la map des séries selon ses variables
     * @param simulationActuelle la simulation sélectionnée
     */
    public void setSimulationActuelle(Simulation simulationActuelle) {
        this.simulationActuelle = simulationActuelle;
        listViewVariablesSimulateur.getItems().addAll(simulationActuelle.getPlusRecentEtat().getVariableList());
        for(int i = 0; i < simulationActuelle.getPlusRecentEtat().getVariableList().size(); i++){
            seriesMap.put(this.simulationActuelle.getPlusRecentEtat().getVariableList().get(i).getName(), new XYChart.Series());
            seriesMap.get(this.simulationActuelle.getPlusRecentEtat().getVariableList().get(i).getName()).setName(simulationActuelle.getPlusRecentEtat().getVariableList().get(i).getName());
        }
    }

    /**
     * désactive les champs de modifications des axes
     * @param doDisable vrai si on désactive, faux pour le contraire
     */
    public void setDisableBoundaryControls(boolean doDisable) {
        maxX.setDisable(doDisable);
        maxY.setDisable(doDisable);
    }

    /**
     * permet de désactiver les bouttons de contrôle du simulateur
     * @param doDisable vrai si on désactive, faux pour le contraire
     */
    public void setDisableSimulationControls(boolean doDisable) {
        bouttonArreter.setDisable(doDisable);
        bouttonDemarrer.setDisable(doDisable);
        bouttonModifier.setDisable(doDisable);
    }

    /**
     * permet de définir les limites des axes selon ce qui est contenu dans les champs à cet égard
     */
    public void setGraphBoundaries() {
        if (rangeStatique.isSelected()) { //si la case d'axes statiques est sélectionnée
            //si une seule case est vide, l'axe qui lui est associé sera en mode automatique et l'autre sera configuré
            //selon ce qui est inscrit dans sa boite de text
            if (maxY.getText().isEmpty() && !maxX.getText().isEmpty()) {
                setXBoundaries(maxX.getText());
                yAxis.setAutoRanging(true);
            } else if (maxX.getText().isEmpty() && !maxY.getText().isEmpty()) {
                setYBoundaries(maxY.getText());
                xAxis.setAutoRanging(true);
            } else if (!maxX.getText().isEmpty() && !maxY.getText().isEmpty()) {
                setXBoundaries(maxX.getText());
                setYBoundaries(maxY.getText());
            } else if (maxX.getText().isEmpty() && maxY.getText().isEmpty()) {
                rangeStatique.setSelected(false);
                xAxis.setAutoRanging(true);
                yAxis.setAutoRanging(true);
            }
        } else { //sinon on met les deux axes en mode automatique
            xAxis.setAutoRanging(true);
            yAxis.setAutoRanging(true);
        }
    }

    /**
     * ajuste les limites de l'axe des X si la boite de text n'est pas vide et contient une valeur valables
     * @param textFieldInput le text field associé à l'axe des X
     */
    public void setXBoundaries(String textFieldInput) {
        if (isValidDouble(textFieldInput)) {
            double boundary = Double.parseDouble(textFieldInput);
            xAxis.setAutoRanging(false);//on désactive le mode auto
            xAxis.setUpperBound(boundary);//on définit la limite
            xAxis.setTickUnit(boundary / 20);//on définit le nombre de coches sur les axes (ça a un nom mais j'ai oublié)
        } else {
            xAxis.setAutoRanging(true); //sinon on met le mode automatique
        }
    }
    /**
     * ajuste les limites de l'axe des X si la boite de text n'est pas vide et contient une valeur valables
     * @param textFieldInput le text field associé à l'axe des Y
     */
    public void setYBoundaries(String textFieldInput) {
        //idem setXboundarie mais pour l'axes de Y
        if (isValidDouble(textFieldInput)) {
            double boundary = Double.parseDouble(textFieldInput);
            yAxis.setAutoRanging(false);
            yAxis.setUpperBound(boundary);
            yAxis.setTickUnit(boundary / 20);
        }
    }


    /**
     * Permet de vérifier si une chaîne de caractère est un double.
     * @param string la chaîne de caractère à évaluer
     * @return vrai si c'est un nombre, faux pour le contraire
     */
    public boolean isValidDouble(String string) {
        boolean isValid = false;
        try {
            Double.parseDouble(string);
            isValid = true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return isValid;
    }

    /**
     * Permet de modifier la valeur d'une variable en ouvrant le dialogue de modification des variables
     * @param actionEvent le clique du boutton modifier
     * @throws IOException exception lancée par le dialogue
     */
    public void modifierVariable(ActionEvent actionEvent) throws IOException {
        if (getVariableIndex() != null) {
            Variable variable = DialoguesUtils.dialogueVariable(true, getVariableIndex());
            listViewVariablesSimulateur.refresh();
        }

    }

    /**
     * Classe Cellfactory des variables
     */
    static class varCell extends ListCell<Variable> {
        private String stringAAfficher;
        private HBox hBox;
        private Label label;

        /**
         * Constructeur de la classe varCell
         */
        public varCell() {
            label = new Label();
            hBox = new HBox(label);
        }

        /**
         * update l'affichage et l'item de la cellule selon la variables reçue en argument.
         * @param variable The new item for the cell.
         * @param empty    whether or not this cell represents data from the list. If it
         *                 is empty, then it does not represent any domain data, but is a cell
         *                 being used to render an "empty" row.
         */
        @Override
        public void updateItem(Variable variable, boolean empty) {
            super.updateItem(variable, empty);

            if (variable == null || empty) {
                setItem(null);
                setGraphic(null);
            } else {
                stringAAfficher = variable.getName();
                setItem(variable);
                label.setText(stringAAfficher + " = " + variable.getValue());
                setGraphic(hBox);
            }
        }
    }

}