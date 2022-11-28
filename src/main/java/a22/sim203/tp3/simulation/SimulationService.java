package a22.sim203.tp3.simulation;

import a22.sim203.tp3.DialoguesUtils;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.atomic.AtomicBoolean;


public class SimulationService extends Service<Etat> {

    private Simulation simulation;

    private Etat etatActuel;

    private Etat etatInitial;

    private long dt;

    private long dtTheorique;

    private double t;

    private boolean stop = false;



    public SimulationService(String name, Etat etatInitial) {
        super();
        this.simulation = new Simulation("name", etatInitial);
        this.etatInitial = simulation.getHistorique(0);
        setEtatActuel(etatInitial);
    }

    public SimulationService(String name, Simulation simulation) {
        super();
        this.simulation = simulation;
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

    public void setTempsEtIntervalTheorique(int t, int dth) {
        this.dtTheorique = dth;
        this.t = t;
    }

    public Double getT() {
        return this.t;
    }

    public void setT(long t){
        this.t = t;

    };

    public long getDt() {
        return this.dt;
    }


    public void setStop() {
        this.stop = !stop;


    }

    public void resetAll(){
        setSimulation(new Simulation("sim", etatInitial));
        setT(0);
        setEtatActuel(new Etat(etatInitial));
    }

    public AtomicBoolean save(Stage ownerWindow){
        AtomicBoolean success = new AtomicBoolean(false);

        ObjectOutputStream bw = null;
        try {
            File fichierDeSauvegarde = DialoguesUtils.fileChooserDialog(ownerWindow);

            if(fichierDeSauvegarde == null){
                System.out.println("Fichier inexistant, fichier simulation.sim sera utilisé");
                fichierDeSauvegarde = new File("simulation.sim");
            }

            bw = new ObjectOutputStream(new FileOutputStream(fichierDeSauvegarde));
            bw.writeObject(simulation);
            success.set(true);
            bw.close();


        } catch (Exception e) {
            System.out.println("failed");
        }


        return success; //
    }


    public class SimulationServiceTask extends Task<Etat> {
        @Override
        protected Etat call() throws Exception {
            Etat nouvelEtat;
            long oldT = 0;


            while (!stop && !isCancelled()) {
                updateValue(etatActuel);
                dt = System.currentTimeMillis() - oldT;
                nouvelEtat = simulation.simulateStep(t, (double) dt, etatActuel);

                Thread.sleep(dtTheorique * 1000);

                t = (double) (System.currentTimeMillis() / 1000);
                etatActuel = nouvelEtat;
                oldT = System.currentTimeMillis();
            }

            save();

            return etatActuel;
        }
    }
}
