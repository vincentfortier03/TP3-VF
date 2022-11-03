package a22.sim203.tp3.controlleurs;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class SimulateurController {

    @FXML
    private Button bouttonArreter;

    @FXML
    private Button bouttonDemarrer;

    @FXML
    private Button bouttonModifier;

    @FXML
    private LineChart<?, ?> lineChartSimulateur;

    @FXML
    private TextField testFieldTemps;

}