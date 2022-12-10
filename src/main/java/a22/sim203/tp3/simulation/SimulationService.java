package a22.sim203.tp3.simulation;

import javafx.concurrent.Service;
import javafx.concurrent.Task;


public class SimulationService extends Service<Etat> {

    private Simulation simulation;

    private Etat etatActuel;

    private Etat etatInitial;

    private long dt;

    private double dtTheorique;

    private double t = 0;

    private boolean stop = false;

    private double timeBoundary;

    private double valueBoundary;

    private Variable curentlySimulatedVariable;


    public SimulationService(String name, Etat etatInitial) {
        super();
        this.simulation = new Simulation("name", etatInitial);
        this.etatInitial = simulation.getHistorique(simulation.getHistorique().size() - 1);
        setEtatActuel(etatInitial);

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

    public void setIntervalTheorique(int dth) {
        this.dtTheorique = dth;
    }

    public double getT() {
        return this.t / 1000;
    }


    public void setStop() {
        this.stop = !stop;
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
            while (!stop && !isCancelled() && 0 <= etatActuel.getVariable(curentlySimulatedVariable.getName()).getValue() && etatActuel.getVariable(curentlySimulatedVariable.getName()).getValue() <= valueBoundary && t/1000 <= timeBoundary) {
                Thread.sleep((long) (dtTheorique * 1000));

                dt = System.currentTimeMillis() - oldT;
                t += dt;
                nouvelEtat = simulation.simulateStep(t/1000, (double) dt, etatActuel);

                etatActuel = nouvelEtat;
                oldT = System.currentTimeMillis();
                updateValue(etatActuel);
            }

            return etatActuel;
        }
    }
}
