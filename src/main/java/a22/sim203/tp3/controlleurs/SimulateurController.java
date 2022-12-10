package a22.sim203.tp3.controlleurs;

import a22.sim203.tp3.simulation.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

import javax.xml.crypto.Data;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
    private LineChart<Number,Number> lineChartSimulateur;


    private XYChart.Series<Number, Number> serie;

    private SimulationService simService;

    private Simulation simulation;

    private Equation equation;

    private int dtTheorique = 1;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        serie = new XYChart.Series<>();
        serie.setName("seriePirncipale");
        lineChartSimulateur.getData().add(serie);


        listViewVariablesSimulateur.setCellFactory((e) -> new varCell());
    }

    @FXML
    public void simStart(ActionEvent event){
        simService = new SimulationService("FE", new Etat(simulation.getLastEtat()));
        simService.setIntervalTheorique(this.dtTheorique);

        if(listViewVariablesSimulateur.getSelectionModel().getSelectedItem() != null){
            setGraphBoundaries(30, listViewVariablesSimulateur.getSelectionModel().getSelectedItem());
        }
        serie.setName(listViewVariablesSimulateur.getSelectionModel().getSelectedItem().getName());

        simService.setOnRunning((event1 -> {
            listViewVariablesSimulateur.setDisable(true);
        }));

        simService.setOnSucceeded((i) ->{
            System.out.println("complété");
            listViewVariablesSimulateur.setDisable(false);
            listViewVariablesSimulateur.refresh();
            simService.setStop();
            serie.getData().clear();
        });

        simService.valueProperty().addListener((a,o,n) -> {
            System.out.println(n.getVariableList());
            if(listViewVariablesSimulateur.getSelectionModel().getSelectedItem() != null){
               serie.getData().add( new XYChart.Data<>(simService.getT(), n.getVariable(listViewVariablesSimulateur.getSelectionModel().getSelectedItem().getName()).getValue()));
            }
            testFieldTemps.setText(""+simService.getT());
        });

        simService.start();
    }

    @FXML
    public void simStop(ActionEvent event){
        if (simService != null && simService.isRunning() && simService.getState() != Worker.State.CANCELLED){
            simService.setStop();
        }
    }

    public void setSimulation(Simulation simulation){
        this.simulation = simulation;
        listViewVariablesSimulateur.getItems().addAll(simulation.getLastEtat().getVariableList());
    }

    public void setEquation(Equation equation){
        this.equation = equation;
    }


    @FXML
    void saveToCustomFile(ActionEvent event) {

    }

    public void setGraphBoundaries(double t, Variable variable){
        Variable variableCopy = new Variable(variable);
        double boundary = 0;
        for(int i = 0; i < simulation.getFucntionListFromVariable(variableCopy).size(); i++){
            Function fct = simulation.getFucntionListFromVariable(variableCopy).get(i);
            for(int j = 0; j <= t; j++)
                boundary = fct.calculate(new Argument(variableCopy.getName()));
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