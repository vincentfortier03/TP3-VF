package a22.sim203.tp3.simulation;

import javafx.concurrent.ScheduledService;
import javafx.concurrent.Service;
import javafx.concurrent.Task;


public class SimulationService extends Service<Etat> {

    private Simulation simulation;

    private Etat etatActuel;

    private long dt;

    private long dtTh;

    private double t;

    private boolean stop = false;

    private long oldT;

    public SimulationService(String name){
        super();
        simulation = new Simulation("name");
    }

    @Override
    protected Task<Etat> createTask() {
        return new SimulationServiceTask();
    }

    public Simulation getSimulation() {
        return simulation;
    }

    public void setEtatActuel(Etat etatActuel) {
        this.etatActuel = etatActuel;
    }

    public void setTempsEtIntervalTheorique(int t, int dt) {
        this.dtTh = dt;
        this.t = t;
    }

    public Double getT(){
        return this.t;
    }

    public long getDt(){
        return this.dt;
    }

    public class SimulationServiceTask extends Task<Etat>{
        @Override
        protected Etat call() throws Exception {
            Etat nouvelEtat = null;
            //Update initiale
            while(!stop){
                updateValue(etatActuel);
                dt = System.currentTimeMillis() - oldT;
                nouvelEtat = simulation.simulateStep(t,(double)dt,etatActuel);
                Thread.sleep(dtTh*1000);
                t = (double)(System.currentTimeMillis()/1000);
                etatActuel = nouvelEtat;
            }

            return nouvelEtat;
        }
    }
}
