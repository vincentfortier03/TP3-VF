package a22.sim203.tp3.simulation;

import org.mariuszgromada.math.mxparser.Argument;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Etat implements Serializable {
    private List<Variable> variableList = new ArrayList<>();

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


    public List<Variable> getVariableList() {
        return variableList;
    }

    public void addVariable(Variable variable) {
        variableList.add(variable);
    }

    public double getValFor(Argument argCherche) {
        double retVal = Double.NaN;
        for (Variable var : variableList) {
            if (var.getName().equals(argCherche.getArgumentName())) {
                retVal = var.getValue();
            }
        }
        return retVal;
    }

    @Override
    public String toString() {
        return "Etat{" +
                "variableList=" + variableList +
                '}';
    }

    public void setVariableList(List<Variable> variableList) {
        this.variableList = variableList;
    }

}
