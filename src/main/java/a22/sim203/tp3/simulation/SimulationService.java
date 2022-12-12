package a22.sim203.tp3.simulation;

import javafx.concurrent.Service;
import javafx.concurrent.Task;


/**
 * Classe du service SimulationService
 *
 * @author Vincent Fortier
 */
public class SimulationService extends Service<Etat> {

    /**
     * la simulation du service
     */
    private Simulation simulation;

    /**
     * L'état actuel du service
     */
    private Etat etatActuel;

    /**
     * l'interval de temps réel
     */
    private long interval;

    /**
     * interval de temps théorique
     */
    private double intervalTheorique;

    /**
     * le temps actuel de la simulation
     */
    private float tempsActuel = 0;

    /**
     * booléen indiquant si la simulation doit s'arrêter
     */
    private boolean stop = false;

    /**
     * la limite de temps du service
     */
    private double timeBoundary;

    /**
     * la valeure maximale que peut atteindre le service
     */
    private double valueBoundary;

    /**
     * la Variable qui est actuellement affichée dans le simulateur
     */
    private Variable curentlySimulatedVariable;


    /**
     * Constructeur de la classe SimulationService. Crée un nouveau service en créant une nouvelle simulation selon les informations reçues en argument
     * @param name le nom de la simulation du service
     * @param etatInitial l'état de départ du service
     * @param tempsInitial le temps initial
     * @param intervalTheorique l'interval théorique
     */
    public SimulationService(String name, Etat etatInitial, float tempsInitial, double intervalTheorique) {
        super();
        this.simulation = new Simulation(name, etatInitial);
        setEtatActuel(etatInitial);
        this.tempsActuel = tempsInitial;
        this.intervalTheorique = intervalTheorique;

        setValueBoundary(Double.MAX_VALUE);
        setTimeBoundary(Double.MAX_VALUE);
    }

    /**
     * Crée une nouvelle tâche SimulationServiceTask
     * @return une tâche SimulationServiceTask
     */
    @Override
    protected Task<Etat> createTask() {
        return new SimulationServiceTask();
    }

    /**
     * définit l'état actuel du service
     * @param etatActuel l'état actuel à définir
     */
    public void setEtatActuel(Etat etatActuel) {
        this.etatActuel = etatActuel;
    }

    /**
     * retourne l'interval théorique du service
     * @return l'interval théorique du service
     */
    public double getIntervalTheorique() {
       return this.intervalTheorique;
    }

    /**
     * retourne le temps actuel du service en secondes
     * @return le temps actuel du service en secondes
     */
    public double getTempsEnSecondes() {
        return this.tempsActuel /1000;
    }

    /**
     * retourne le temps actuel du service
     * @return le temps actuel
     */
    public float getTempsActuel() {
        return this.tempsActuel;
    }

    /**
     * Permet d'inverser la variable stop
     */
    public void setStop() {
        this.stop = !stop;
    }

    /**
     * permet de retourne la valeur de la variable stop
     * @return la valeur de stop
     */
    public boolean getStop() {
        return this.stop;
    }

    /**
     * permet de définir la valeure maximale que peut atteindre le service
     * @param valueBoundary la valeure maximale
     */
    public void setValueBoundary(double valueBoundary) {
        this.valueBoundary = valueBoundary;
    }

    /**
     * permet de définir le temps maximal que peut atteindre le service
     * @param timeBoundary le temps limite
     */
    public void setTimeBoundary(double timeBoundary) {
        this.timeBoundary = timeBoundary;
    }

    /**
     * permet de définir la variable actuellement affichée dans le simulateur
     * @param curentlySimulatedVariable la variable actuellement affichée dans le simulateur
     */
    public void setCurentlySimulatedVariable(Variable curentlySimulatedVariable) {
        this.curentlySimulatedVariable = curentlySimulatedVariable;
    }


    /**
     * Classe interne de la tache SimulationServiceTask
     */
    public class SimulationServiceTask extends Task<Etat> {
        /**
         * Permet de calculer des nouveaux états à répetition jusqu'à ce que le service soit arrêté, ou que celui a
         * atteint les limites valueBoundary et timeBoundary
         * @return le dernier état simulé
         * @throws Exception
         */
        @Override
        protected Etat call() throws Exception {
            Etat nouvelEtat;
            long tempsOLD = System.currentTimeMillis();

            updateValue(etatActuel);
            while (!stop && !isCancelled() && 0 <= etatActuel.getVariable(curentlySimulatedVariable.getName()).getValue() && etatActuel.getVariable(curentlySimulatedVariable.getName()).getValue() <= valueBoundary && (double)(tempsActuel /(float)1000) <= timeBoundary) {
                Thread.sleep((long) (intervalTheorique * 1000));

                interval = System.currentTimeMillis() - tempsOLD;
                tempsActuel += (float) interval;
                nouvelEtat = simulation.simulateStep(tempsActuel /1000, (double) interval, etatActuel);

                etatActuel = nouvelEtat;
                tempsOLD = System.currentTimeMillis();
                updateValue(etatActuel);
            }
            return etatActuel;
        }
    }
}
