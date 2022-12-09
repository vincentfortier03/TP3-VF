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

import java.net.URL;
import java.util.ArrayList;
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
    private LineChart<String,Double> lineChartSimulateur;

    @FXML
    private CategoryAxis xAxis;
    @FXML
    private NumberAxis yAxis;

    private XYChart.Series<String, Double> serie;


    private ObservableList<XYChart.Series<String,Double>> seriesList;

    private ObservableList<XYChart.Data<String, Double>> dataList;


    private SimulationService simService;

    private Simulation simulation;

    private Equation equation;

    private float t;

    private int dtTheorique;

    private XYChart.Data data;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setTDT(0,1);
        dataList = FXCollections.observableList(new ArrayList<>());
        serie = new XYChart.Series<>();
        seriesList = FXCollections.observableList(new ArrayList<>());
        lineChartSimulateur.setData(seriesList);

        yAxis.setUpperBound(100);
        yAxis.setLowerBound(0);
        yAxis.setTickUnit(10);
        yAxis.setMinorTickCount(0);

        yAxis.setAutoRanging(false);
        xAxis.setAutoRanging(false);

        listViewVariablesSimulateur.setCellFactory((e) -> new varCell());
    }

    @FXML
    public void simStart(ActionEvent event){
        simService = new SimulationService("FE", new Etat(simulation.getLastEtat()));
        simService.setIntervalTheorique(this.t,this.dtTheorique);

        serie = new XYChart.Series<>();
        serie.setName(listViewVariablesSimulateur.getSelectionModel().getSelectedItem().getName());
        serie.setData(dataList);
        seriesList.add(serie);


        simService.setOnRunning((event1 -> {
            listViewVariablesSimulateur.setDisable(true);
        }));

        simService.setOnSucceeded((i) ->{
            System.out.println("complété");
            listViewVariablesSimulateur.setDisable(false);
            listViewVariablesSimulateur.refresh();
            simService.setStop();
            dataList.clear();
            seriesList.clear();
        });

        simService.valueProperty().addListener((a,o,n) -> {
//            listViewVariablesSimulateur.getItems().clear();
//            listViewVariablesSimulateur.getItems().addAll(n.getVariableList());
//            listViewVariablesSimulateur.refresh();
//            System.out.println(n.getVariableList());
            System.out.println(lineChartSimulateur);
            if(listViewVariablesSimulateur.getSelectionModel().getSelectedItem() != null){
                dataList.add(new XYChart.Data<String,Double>(""+simService.getT(), n.getVariable(listViewVariablesSimulateur.getSelectionModel().getSelectedItem().getName()).getValue()));
                System.out.println("t = " +simService.getT() + ", v = " + n.getVariable(listViewVariablesSimulateur.getSelectionModel().getSelectedItem().getName()).getValue());
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

    public void setTDT(int t, int dtTheorique){
        this.t = t;
        this.dtTheorique = dtTheorique;

    }
    private void setupLineChart(){

        simService.valueProperty().addListener(new ChangeListener<Etat>() {
            @Override
            public void changed(ObservableValue<? extends Etat> observable, Etat oldValue, Etat newValue) {

            }
        });


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