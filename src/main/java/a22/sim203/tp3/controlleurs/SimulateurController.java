package a22.sim203.tp3.controlleurs;

import a22.sim203.tp3.DialoguesUtils;
import a22.sim203.tp3.simulation.*;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;

public class SimulateurController implements Initializable {

    @FXML
    private BorderPane root;

    @FXML
    private Button bouttonArreter;

    @FXML
    private Button bouttonDemarrer;

    @FXML
    private Button bouttonModifier;


    @FXML
    private Button bouttonPause;

    @FXML
    private TextField textFieldTemps;

    @FXML
    private ListView<Variable> listViewVariablesSimulateur;

    @FXML
    private TextField maxAuto;

    @FXML
    private TextField maxX;

    @FXML
    private TextField maxY;

    @FXML
    private CheckBox rangeStatique;

    @FXML
    private TextField textFieldInterval;

    private LineChart<Number, Number> lineChartSimulateur;

    private NumberAxis xAxis;

    private NumberAxis yAxis;


    private XYChart.Series<Number, Number> serieActuelle;

    private SimulationService serviceActuel;

    private SimulationService serviceAvantPause;

    private Simulation simulationActuelle;

    private float pauseTime;

    private Etat etatAvantPause;

    private double dtAvantPause;

    private boolean isPaused = false;

    private int variableIndex = 0;
    private Equation equationSelectionnee;

    private Map<String, XYChart.Series> seriesMap;
    private double intervalTheorique;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        xAxis = new NumberAxis();
        yAxis = new NumberAxis();

        lineChartSimulateur = new LineChart<>(xAxis, yAxis);
        xAxis.setAutoRanging(false);
        yAxis.setAutoRanging(true);
        xAxis.setLowerBound(0);
        yAxis.setLowerBound(0);
        root.setCenter(lineChartSimulateur);
        listViewVariablesSimulateur.setCellFactory((e) -> new varCell());
        maxY.setText("");
        maxX.setText("");
        seriesMap = new TreeMap<>();

        setDisableBoundaryControls(true);
        rangeStatique.selectedProperty().addListener((e) -> {
            setDisableBoundaryControls(!rangeStatique.isSelected());
        });

        listViewVariablesSimulateur.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<Variable>() {
            @Override
            public void onChanged(Change<? extends Variable> c) {
                setDisableSimulationControls(listViewVariablesSimulateur.getSelectionModel().isEmpty());
                try {
                    updateGraph();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(lineChartSimulateur.getData());
            }
        });


    }

    @FXML
    void simPause(ActionEvent event) {
        if (!serviceActuel.getStop() && serviceActuel.getState() == Worker.State.RUNNING) {
            serviceActuel.setStop();
            isPaused = true;
            pauseTime = serviceActuel.getT();
            dtAvantPause = serviceActuel.getIntervalTheorique();
            etatAvantPause = new Etat(serviceActuel.getValue());
        }
    }

    public Variable getVariableIndex() {
        return listViewVariablesSimulateur.getItems().get(variableIndex);
    }

    public void updateGraph() throws InterruptedException {
        lineChartSimulateur.setAnimated(false);
        lineChartSimulateur.getData().clear();
        lineChartSimulateur.getData().add(seriesMap.get(listViewVariablesSimulateur.getSelectionModel().getSelectedItem().getName()));
        lineChartSimulateur.setAnimated(true);
    }


    @FXML
    public void simStart(ActionEvent event) {
        if (isPaused) {
            serviceActuel = new SimulationService(simulationActuelle.getName(), etatAvantPause,pauseTime,dtAvantPause);
            isPaused = false;
        } else{
            clearSeries();
            modifierIntervalTheorique();
            serviceActuel = new SimulationService(simulationActuelle.getName(), new Etat(simulationActuelle.getLastEtat()),0,getIntervalTheorique());
            textFieldInterval.setText(serviceActuel.getIntervalTheorique()+"");
        }

        setGraphBoundaries();
        if (getVariableIndex() != null) {
            serviceActuel.setCurentlySimulatedVariable(getVariableIndex());

            if (!xAxis.isAutoRanging()) {
                serviceActuel.setTimeBoundary(xAxis.getUpperBound());
            }

            if (!yAxis.isAutoRanging()) {
                serviceActuel.setValueBoundary(yAxis.getUpperBound());
            }
            serviceActuel.start();
        }

        serviceActuel.setOnRunning((event1 -> {
            textFieldInterval.setDisable(true);
            setDisableBoundaryControls(true);
        }));

        serviceActuel.setOnSucceeded((i) -> {
            activerUI();
        });

        setupValueListener();
    }

    public void modifierIntervalTheorique(){
        double intervalTheorique = 1;
        if(validDouble(textFieldInterval.getText()) && Double.parseDouble(textFieldInterval.getText())>0){
            intervalTheorique = Double.parseDouble(textFieldInterval.getText());
        }
        this.intervalTheorique = intervalTheorique;
    }

    public double getIntervalTheorique(){
        return this.intervalTheorique;
    }

    public void clearSeries(){
        for(int i = 0; i < seriesMap.size(); i++){
            seriesMap.get(listViewVariablesSimulateur.getItems().get(i).getName()).getData().clear();
        }
    }


    public void setupValueListener(){
        serviceActuel.valueProperty().addListener((a, o, n) -> {
            for(int i = 0; i<n.getVariableList().size(); i++) {
                if(rangeStatique.isSelected() && n.getVariableList().get(i).getValue()>yAxis.getUpperBound()){
                    simStop(new ActionEvent());
                }else {
                    seriesMap.get(n.getVariableList().get(i).getName()).getData().add(new XYChart.Data<>(serviceActuel.getTSecondes(), serviceActuel.getValue().getVariable(n.getVariableList().get(i).getName()).getValue()));
                }

            }
            textFieldTemps.setText("" + serviceActuel.getTSecondes());
        });
    }

    @FXML
    public void simStop(ActionEvent event) {
        if (serviceActuel != null && serviceActuel.isRunning() && serviceActuel.getState() != Worker.State.CANCELLED) {
            serviceActuel.cancel();
            activerUI();
        }
    }

    public void activerUI(){
        if(rangeStatique.isSelected()){
            maxX.setDisable(false);
            maxY.setDisable(false);
        }
        textFieldInterval.setDisable(false);
    }

    public void setEquationSelectionnee(Equation equationSelectionnee) {
        this.equationSelectionnee = equationSelectionnee;
    }

    public void setSimulationActuelle(Simulation simulationActuelle) {
        this.simulationActuelle = simulationActuelle;
        listViewVariablesSimulateur.getItems().addAll(simulationActuelle.getLastEtat().getVariableList());
        for(int i = 0; i < simulationActuelle.getLastEtat().getVariableList().size(); i++){
            seriesMap.put(this.simulationActuelle.getLastEtat().getVariableList().get(i).getName(), new XYChart.Series());
            seriesMap.get(this.simulationActuelle.getLastEtat().getVariableList().get(i).getName()).setName(simulationActuelle.getLastEtat().getVariableList().get(i).getName());
        }
    }

    public void setDisableBoundaryControls(boolean doDisable) {
        maxX.setDisable(doDisable);
        maxY.setDisable(doDisable);
    }

    public void setDisableSimulationControls(boolean doDisable) {
        bouttonArreter.setDisable(doDisable);
        bouttonDemarrer.setDisable(doDisable);
        bouttonModifier.setDisable(doDisable);
    }

    public void setGraphBoundaries() {
        if (rangeStatique.isSelected()) {
            if (maxY.getText().isEmpty() && !maxX.getText().isEmpty()) {
                setXBoundaries(maxX.getText());
                yAxis.setAutoRanging(true);
                System.out.println("set x static");
            } else if (maxX.getText().isEmpty() && !maxY.getText().isEmpty()) {
                setYBoundaries(maxY.getText());
                xAxis.setAutoRanging(true);
                System.out.println("set y static");
            } else if (!maxX.getText().isEmpty() && !maxY.getText().isEmpty()) {
                setXBoundaries(maxX.getText());
                setYBoundaries(maxY.getText());
                System.out.println("set x y static");
            } else if (maxX.getText().isEmpty() && maxY.getText().isEmpty() && maxAuto.getText().isEmpty()) {
                rangeStatique.setSelected(false);
                xAxis.setAutoRanging(true);
                yAxis.setAutoRanging(true);
            }
        } else {
            xAxis.setAutoRanging(true);
            yAxis.setAutoRanging(true);
        }
    }

    public void setXBoundaries(String textFieldInput) {
        if (validDouble(textFieldInput)) {
            double boundary = Double.parseDouble(textFieldInput);
            xAxis.setAutoRanging(false);
            xAxis.setUpperBound(boundary);
            xAxis.setTickUnit(boundary / 20);
        } else {
            xAxis.setAutoRanging(true);
            System.out.println("axe des X en mode automatique");
        }
    }

    public void setYBoundaries(String textFieldInput) {
        if (validDouble(textFieldInput)) {
            double boundary = Double.parseDouble(textFieldInput);
            yAxis.setAutoRanging(false);
            yAxis.setUpperBound(boundary);
            yAxis.setTickUnit(boundary / 20);
        } else {
            System.out.println("axe des Y en mode automatique");
        }
    }


    public boolean validDouble(String string) {
        boolean isValid = false;
        try {
            Double.parseDouble(string);
            isValid = true;

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return isValid;
    }

    public void modifierVariable(ActionEvent actionEvent) throws IOException {
        if (getVariableIndex() != null) {
            Variable variable = DialoguesUtils.dialogueVariable(true, getVariableIndex());
            listViewVariablesSimulateur.refresh();
        }

    }

    static class varCell extends ListCell<Variable> {
        private String stringAAfficher;
        private HBox hBox;
        private Label label;

        public varCell() {
            label = new Label();
            hBox = new HBox(label);
        }

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