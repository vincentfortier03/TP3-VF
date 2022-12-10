package a22.sim203.tp3.controlleurs;

import a22.sim203.tp3.DialoguesUtils;
import a22.sim203.tp3.simulation.*;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import org.mariuszgromada.math.mxparser.Argument;
import org.mariuszgromada.math.mxparser.Function;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

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
    private TextField testFieldTemps;

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

    private LineChart<Number, Number> lineChartSimulateur;

    private NumberAxis xAxis;

    private NumberAxis yAxis;


    private XYChart.Series<Number, Number> serie;

    private SimulationService simService;

    private Simulation simulation;

    private Equation equation;

    private int dtTheorique = 1;


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

        serie = new XYChart.Series<>();
        serie.setName("seriePirncipale");
        lineChartSimulateur.getData().add(serie);

        listViewVariablesSimulateur.setCellFactory((e) -> new varCell());

        maxY.setText("");
        maxX.setText("");
        maxAuto.setText("");

        setDisableBoundaryControls(true);
        rangeStatique.selectedProperty().addListener((e)->{
            setDisableBoundaryControls(!rangeStatique.isSelected());
        });

        listViewVariablesSimulateur.selectionModelProperty().addListener((a,s,d)->{
            setDisableSimulationControls(listViewVariablesSimulateur.getSelectionModel().isEmpty());
        });
    }

    @FXML
    public void simStart(ActionEvent event) {
        simService = new SimulationService("FE", new Etat(simulation.getLastEtat()));
        simService.setIntervalTheorique(this.dtTheorique);

        Variable currentlySelectedVariable = listViewVariablesSimulateur.getSelectionModel().getSelectedItem();

        if (currentlySelectedVariable != null) {
            serie.setName(currentlySelectedVariable.getName());
            setGraphBoundaries(currentlySelectedVariable);
            simService.setCurentlySimulatedVariable(currentlySelectedVariable);

            if(!xAxis.isAutoRanging()){
                simService.setTimeBoundary(xAxis.getUpperBound());
            }

            if(!yAxis.isAutoRanging()){
                simService.setValueBoundary(yAxis.getUpperBound());
            }

            simService.start();
        }

        simService.setOnRunning((event1 -> {
            listViewVariablesSimulateur.setDisable(true);
            setDisableBoundaryControls(true);
        }));

        simService.setOnSucceeded((i) -> {
            System.out.println("complété");
            listViewVariablesSimulateur.setDisable(false);
            if(rangeStatique.isSelected()){
                setDisableBoundaryControls(false);
            }

            if(xAxis.isAutoRanging()){
                xAxis.setUpperBound(simService.getT()+xAxis.getTickUnit());
            }

            serie.getData().add(new XYChart.Data<>(simService.getT(), simService.getValue().getVariable(currentlySelectedVariable.getName()).getValue()));
            listViewVariablesSimulateur.refresh();
            simService.setStop();
            serie.getData().clear();
        });

        simService.valueProperty().addListener((a, o, n) -> {
            System.out.println(n.getVariableList());
            serie.getData().add(new XYChart.Data<>(simService.getT(), simService.getValue().getVariable(currentlySelectedVariable.getName()).getValue()));

            testFieldTemps.setText("" + simService.getT());

//            if(!xAxis.isAutoRanging() && n.getVariable(nomVariableSelectionnee).getValue() >= xAxis.getUpperBound()){
//                xAxis.setUpperBound(simService.getT());
//                yAxis.setUpperBound(n.getVariable(nomVariableSelectionnee).getValue());
//                simStop(new ActionEvent());
//            }else if(!yAxis.isAutoRanging() && n.getVariable(nomVariableSelectionnee).getValue() >= yAxis.getUpperBound()){
//                xAxis.setUpperBound(simService.getT());
//                yAxis.setUpperBound(simService.getSimulation().getLastEtat().getVariable(nomVariableSelectionnee).getValue());
//                simStop(new ActionEvent());
//            }
        });
    }

    @FXML
    public void simStop(ActionEvent event) {
        if (simService != null && simService.isRunning() && simService.getState() != Worker.State.CANCELLED) {
            simService.setStop();
        }
    }

    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
        listViewVariablesSimulateur.getItems().addAll(simulation.getLastEtat().getVariableList());
    }

    public void setEquation(Equation equation) {
        this.equation = equation;
    }


    @FXML
    void saveToCustomFile(ActionEvent event) {

    }

    public void setDisableBoundaryControls(boolean doDisable){
        maxX.setDisable(doDisable);
        maxY.setDisable(doDisable);
        maxAuto.setDisable(doDisable);
    }

    public void setDisableSimulationControls(boolean doDisable){
        bouttonArreter.setDisable(doDisable);
        bouttonDemarrer.setDisable(doDisable);
        bouttonModifier.setDisable(doDisable);
    }

    public void setGraphBoundaries(Variable variable) {
        if (rangeStatique.isSelected()) {
            if (!maxAuto.getText().isEmpty()) {
                maxX.clear();
                maxY.clear();
                setAutoBoundaries(maxAuto.getText(), variable);
                System.out.println("set to auto");
            }else if (maxY.getText().isEmpty() && !maxX.getText().isEmpty() ) {
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
            }else if(maxX.getText().isEmpty() && maxY.getText().isEmpty() && maxAuto.getText().isEmpty()){
                rangeStatique.setSelected(false);
                xAxis.setAutoRanging(true);
                yAxis.setAutoRanging(true);
            }
        }else{
            xAxis.setAutoRanging(true);
            yAxis.setAutoRanging(true);
        }
    }

    public void setXBoundaries(String textFieldInput){
        if (validDouble(textFieldInput)){
            double boundary = Double.parseDouble(textFieldInput);
            xAxis.setAutoRanging(false);
            xAxis.setUpperBound(boundary);
            xAxis.setTickUnit(boundary / 20);
        }else {
            xAxis.setAutoRanging(true);
            System.out.println("axe des X en mode automatique");
        }
    }

    public void setYBoundaries(String textFieldInput){
        if (validDouble(textFieldInput)){
            double boundary = Double.parseDouble(textFieldInput);
            yAxis.setAutoRanging(false);
            yAxis.setUpperBound(boundary);
            yAxis.setTickUnit(boundary / 20);
        }else{
            System.out.println("axe des Y en mode automatique");
        }
    }

    public void setAutoBoundaries(String textFieldInput, Variable variable){
        if (validDouble(textFieldInput)) {
            double t = Integer.parseInt(textFieldInput);
            Variable variableCopy = new Variable(variable);
            double boundary = 0;
            for (int i = 0; i < simulation.getFucntionListFromVariable(variableCopy).size(); i++) {
                Function fct = simulation.getFucntionListFromVariable(variableCopy).get(i);
                Variable nextStepVar = new Variable(variable);
                for (int j = 0; j <= t; j++) {
                    boundary = fct.calculate(new Argument(variableCopy.getName(), nextStepVar.getValue()));
                    nextStepVar.setValue(boundary);
                }
            }
            xAxis.setAutoRanging(false);
            yAxis.setAutoRanging(false);
            xAxis.setUpperBound(t);
            yAxis.setUpperBound(boundary);
            yAxis.setTickUnit(boundary / 20);
        }else {
            xAxis.setAutoRanging(true);
            yAxis.setAutoRanging(true);
            System.out.println("");
        }
    }

    public boolean validDouble(String string){
       boolean isValid = false;
       try{
           Double.parseDouble(string);
           isValid = true;

       }catch (Exception e){
           System.out.println(e.getMessage());
       }

       return isValid;
    }

    public void modifierVariable(ActionEvent actionEvent) throws IOException {
        if(listViewVariablesSimulateur.getSelectionModel().getSelectedItem() != null){
            Variable variable = DialoguesUtils.dialogueVariable(true, listViewVariablesSimulateur.getSelectionModel().getSelectedItem());
            listViewVariablesSimulateur.refresh();
        }

    }

    static class varCell extends ListCell<Variable> {
        private String stringAAfficher;
        private HBox hBox;
        private Label label;

        public varCell(){
            label = new Label();
            hBox = new HBox(label);
        }

        @Override
        public void updateItem(Variable variable, boolean empty){
            super.updateItem(variable,empty);

            if(variable == null || empty){
                setItem(null);
                setGraphic(null);
            }else{
                stringAAfficher = variable.getName();
                setItem(variable);
                label.setText(stringAAfficher + " = " + variable.getValue());
                setGraphic(hBox);
            }
        }
    }

}