package a22.sim203.tp3.simulation;

import org.mariuszgromada.math.mxparser.Argument;
import org.mariuszgromada.math.mxparser.Function;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Classe de la simulation
 *
 * @author Vincent Fortier
 */
public class Simulation implements Serializable {

    /**
     * nom de la simulation
     */
    private String name;
    /**
     * Liste de tous les étas simulés de la simulation
     */
    private List<Etat> historique = new ArrayList<>();// l'état 0 est le premier état de l'historique

    /**
     * Constructeur de la classe simulation
     * @param name nom de la simulation
     * @param initial état initial de la simulation
     */
    public Simulation(String name, Etat initial) {
        this.name = name;
        ajouteDansHistorique(initial);

    }

    /**
     * Convertis l'équation passée en argument en fonction
     * @param equation équation à convertir
     * @return l'équation sous forme de fonction
     */
    public Function convertEquationToFunction(Equation equation) {
        return new Function(equation.getExpression());
    }

    /**
     * Retourne l'état de l'historique qui est associé à l'indice passé en argument
     * @param step le numéro (ou indice) de l'état à retourne
     * @return l'état associé à l'indice passé en argument
     */
    public Etat getHistorique(int step) {
        Etat retEtat = null;

        if (step <= historique.size()) {
            retEtat = historique.get(step);
        }

        return retEtat;
    }

    /**
     * Retourne l'historique des états simulés de la simulation sous forme de liste
     * @return l'historique sous forme de liste d'états
     */
    public List<Etat> getHistorique() {
        return historique;
    }

    /**
     * Permet de définire l'historique de la simulation
     * @param historique l'historique à définire
     */
    public void setHistorique(List<Etat> historique) {
        this.historique = historique;
    }


    /**
     * retourne le dernier état de l'historique de la simulation
     * @return le dernier état qui a été simulé
     */
    public Etat getPlusRecentEtat() {
        return historique.get(historique.size()-1);
    }

    /**
     * Permet de mettre un état passé en argument dans l'historique de la simulation
     * @param nouvelEtat état à mettre dans l'historique
     */
    public void ajouteDansHistorique(Etat nouvelEtat) {
        historique.add(nouvelEtat);
    }

    /**
     * retourne le nom de la simulation
     * @return le nom de la simulation
     */
    public String getName() {
        return name;
    }

    /**
     * définit le nom de la simulation selon une chaine de caractère passée en arguement
     * @param name le nom voulu
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * crée et retourne une chaine de caractère contenant les informations de la simulation
     * @return une chaine de caractère contenant les informations de la simulation
     */
    @Override
    public String toString() {
        return "Simulation{" +
                "name='" + name + '\'' +
                ", historique=" + historique +
                '}';
    }

    /**
     * Calcul la nouvelle valeur de chaque variable selon les équations qu'elles contiennent et le temps.
     * @param t le temps écoulé depuis le début de la simulation
     * @param dt l'interval de temps depuis le dernier pas de temps
     * @param etatActuel l'état actuel de la simulation
     * @return un nouvel état contenant les variables calculées
     */
    public Etat simulateStep(float t, double dt, Etat etatActuel) {
        Etat nouvelEtat = new Etat(etatActuel);//msd depp copy requise

        for (int i = 0; i < etatActuel.getVariableList().size(); i++) {
            double valueVar = 0;
            for (Equation equationOld : etatActuel.getVariableList().get(i).getEquationsCollection()) {
                Function fctActuelle = convertEquationToFunction(equationOld);
                Argument[] listArgumentsFctActuelle = new Argument[fctActuelle.getArgumentsNumber()];

                for (int j = 0; j < fctActuelle.getArgumentsNumber(); j++) {
                    if(fctActuelle.getArgument(j).getArgumentName().equals("t")){
                        listArgumentsFctActuelle[j] = new Argument(fctActuelle.getArgument(j).getArgumentName(), t);
                    }else{
                        listArgumentsFctActuelle[j] = new Argument(fctActuelle.getArgument(j).getArgumentName(), etatActuel.getVariable(fctActuelle.getArgument(j).getArgumentName()).getValue());
                    }
                }
                valueVar += fctActuelle.calculate(listArgumentsFctActuelle);
            }
            nouvelEtat.getVariable(etatActuel.getVariableList().get(i).getName()).setValue(valueVar);
        }
        ajouteDansHistorique(etatActuel);
        return nouvelEtat;
    }
}
