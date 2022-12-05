package a22.sim203.tp3.simulation;

import javafx.concurrent.Service;
import javafx.concurrent.Task;


public class SimulationService extends Service<Etat> {

    private Simulation simulation;

    private Etat etatActuel;

    private Etat etatInitial;

    private long dt;

    private long dtTheorique;

    private float t = 0;

    private boolean stop = false;


    public SimulationService(String name, Etat etatInitial) {
        super();
        this.simulation = new Simulation("name", etatInitial);
        this.etatInitial = simulation.getHistorique(simulation.getHistorique().size() - 1);
        setEtatActuel(etatInitial);
    }

    public SimulationService(String name, SimulationService simulationService) {
        super();
        this.simulation = new Simulation("name", new Etat(simulationService.getSimulation().getHistorique(simulationService.getSimulation().getHistorique().size() - 1)));
        this.simulation.setHistorique(simulationService.getSimulation().getHistorique());
        this.etatInitial = simulation.getHistorique(0);
        setEtatActuel(etatInitial);
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

    public void setIntervalTheorique(float t, int dth) {
        this.dtTheorique = dth;
    }

    public float getT() {
        return (this.t) / (float) 1000;
    }

    public void setT(long t) {
        this.t = t;

    }

    public long getDt() {
        return this.dt;
    }


    public void setStop() {
        this.stop = !stop;


    }


    public class SimulationServiceTask extends Task<Etat> {
        @Override
        protected Etat call() throws Exception {
            Etat nouvelEtat;
            long oldT = System.currentTimeMillis();

            updateValue(etatActuel);
            while (!stop && !isCancelled()) {
                Thread.sleep(dtTheorique * 1000);

                dt = System.currentTimeMillis() - oldT;
                t += dt;
                nouvelEtat = simulation.simulateStep(t, (double) dt, etatActuel);

                etatActuel = nouvelEtat;
                oldT = System.currentTimeMillis();
                updateValue(etatActuel);
            }

            return etatActuel;
        }
    }
}
