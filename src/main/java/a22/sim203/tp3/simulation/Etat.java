package a22.sim203.tp3.simulation;

import org.mariuszgromada.math.mxparser.Argument;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe d'un état
 *
 * @author Vincent Fortier
 */
public class Etat implements Serializable {
    /**
     * liste des variables de l'état
     */
    private List<Variable> variableList = new ArrayList<>();

    /**
     * Fait une copie en profondeur pour qu'il n'y ait plus de lien vers les arguments originaux
     *
     * @param etatCopie donnees à copier
     */
    public Etat(Etat etatCopie) {
        this.variableList = new ArrayList<>();

        for (Variable var : etatCopie.variableList) {
            variableList.add(new Variable(var));
        }
    }

    /**
     * on crée un nouvel état
     */
    public Etat(){
        this.variableList = new ArrayList<>();
    }

    /**
     * permet d'obtenir la varibale selon son nom
     * @param nom le nom de la variable
     * @return la variable
     */
    public Variable getVariable(String nom) {
        Variable retVariable = null;
        for (Variable var : variableList) {
            if (var.getName().equals(nom)) {
                retVariable = var;
            }
        }
        return retVariable;
    }

    /**
     * permet d'obtenir la liste de toutes les variables de l'état
     * @return une liste contenant les variables
     */
    public List<Variable> getVariableList() {
        return variableList;
    }

    /**
     * permet d'ajouter des varibales à la liste
     * @param variable la variable à ajouter
     */
    public void addVariable(Variable variable) {
        variableList.add(variable);
    }

    /**
     * Permet d'obtenir la variable associée à l'argument passé en argument
     * @param argCherche l'argument de la variable a trouver
     * @return la variable assocée à l'argument
     */
    public double getValFor(Argument argCherche) {
        double retVal = Double.NaN;
        for (Variable var : variableList) {
            if (var.getName().equals(argCherche.getArgumentName())) {
                retVal = var.getValue();
            }
        }
        return retVal;
    }

    /**
     * permet de retourner une chaine de caractère conteant les informations de l'état
     * @return la chaîne de caractère
     */
    @Override
    public String toString() {
        return "Etat{" +
                "variableList=" + variableList +
                '}';
    }
}
