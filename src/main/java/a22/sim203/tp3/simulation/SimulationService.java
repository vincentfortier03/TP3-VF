package a22.sim203.tp3.simulation;

import javafx.concurrent.Service;
import javafx.concurrent.Task;


public class SimulationService extends Service<Etat> {

    private Simulation simulation;

    private Etat etatActuel;

    private long dt;

    private double intervalTheorique;

    private float t = 0;

    private boolean stop = false;

    private double timeBoundary;

    private double valueBoundary;

    private Variable curentlySimulatedVariable;


    public SimulationService(String name, Etat etatInitial, float tempsInitial, double intervalTheorique) {
        super();
        this.simulation = new Simulation(name, etatInitial);
        setEtatActuel(etatInitial);
        this.t = tempsInitial;
        this.intervalTheorique = intervalTheorique;

        setValueBoundary(Double.MAX_VALUE);
        setTimeBoundary(Double.MAX_VALUE);

    }

    @Override
    protected Task<Etat> createTask() {
        return new SimulationServiceTask();
    }

    public Simulation getSimulation() {
        return simulation;
    }

    public void setSimulation(Simulation nouvelleSimulation) {
        this.simulation = nouvelleSimulation;
    }

    public void setEtatActuel(Etat etatActuel) {
        this.etatActuel = etatActuel;
    }

    public double getIntervalTheorique() {
       return this.intervalTheorique;
    }

    public double getTSecondes() {
        return this.t/1000;
    }
    public float getT() {
        return this.t;
    }

    public void setStop() {
        this.stop = !stop;
    }

    public boolean getStop() {
        return this.stop;
    }

    public void setValueBoundary(double valueBoundary) {
        this.valueBoundary = valueBoundary;
    }

    public void setTimeBoundary(double timeBoundary) {
        this.timeBoundary = timeBoundary;
    }

    public void setCurentlySimulatedVariable(Variable curentlySimulatedVariable) {
        this.curentlySimulatedVariable = curentlySimulatedVariable;
    }


    public class SimulationServiceTask extends Task<Etat> {
        @Override
        protected Etat call() throws Exception {
            Etat nouvelEtat;
            long oldT = System.currentTimeMillis();

            updateValue(etatActuel);
            while (!stop && !isCancelled() && 0 <= etatActuel.getVariable(curentlySimulatedVariable.getName()).getValue() && etatActuel.getVariable(curentlySimulatedVariable.getName()).getValue() <= valueBoundary && (double)(t/(float)1000) <= timeBoundary) {
                Thread.sleep((long) (intervalTheorique * 1000));

                dt = System.currentTimeMillis() - oldT;
                t += (float)dt;
                nouvelEtat = simulation.simulateStep(t/1000, (double) dt, etatActuel);

                etatActuel = nouvelEtat;
                oldT = System.currentTimeMillis();
                updateValue(etatActuel);
            }

            return etatActuel;
        }
    }
}
