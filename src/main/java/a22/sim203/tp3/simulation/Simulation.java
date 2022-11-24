package a22.sim203.tp3.simulation;

import org.mariuszgromada.math.mxparser.Argument;
import org.mariuszgromada.math.mxparser.Function;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.*;


public class Simulation implements Serializable {

    private String name;
    private List<Etat> historique = new ArrayList<>();// l'état 0 est le premier état de l'historique

    public Simulation(String name) {
        this.name = name;
    }

    public Etat simulateStep(double t, double dt, Etat etatActuel) {
        Etat nouvelEtat = new Etat(etatActuel);//msd depp copy requise

        ajouteDansHistorique(etatActuel);

        for (int i = 0; i < etatActuel.getVariableList().size(); i++) {
            for (Equation equationOld : etatActuel.getVariableList().get(i).getEquationsCollection()) {
                Function fctActuelle = convertEquationToFunction(equationOld);
                Argument[] listValuesArguments = new Argument[fctActuelle.getArgumentsNumber()];

                for (int j = 0; j < fctActuelle.getArgumentsNumber(); j++) {
                    listValuesArguments[j] = new Argument(fctActuelle.getArgument(j).getArgumentName(), etatActuel.getVariable(fctActuelle.getArgument(j).getArgumentName()).getValue());
                }
               nouvelEtat.getVariable(etatActuel.getVariableList().get(i).getName()).setValue(fctActuelle.calculate(listValuesArguments));
            }
        }

        return nouvelEtat;
    }


    public List<Function> convertEquationsToFunctions(Collection<Equation> equations) {
        List<Function> retList = new ArrayList<>();
        for (Equation equation : equations) {
            retList.add(convertEquationToFunction(equation));
        }
        return retList;
    }

    public Function convertEquationToFunction(Equation equation) {
        return new Function(equation.getExpression());
    }

    public Etat getHistorique(int step) {
        Etat retEtat = null;

        if(step <= historique.size()){
            retEtat = historique.get(step);
        }

        return retEtat;
    }

    public void ajouteDansHistorique(Etat nouvelEtat) {
        historique.add(nouvelEtat);
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Simulation{" +
                "name='" + name + '\'' +
                ", historique=" + historique +
                '}';
    }


    public List<Etat> getHistorique() {
        return historique;
    }

    public void setHistorique(List<Etat> historique) {
        this.historique = historique;
    }

    public static void main(String[] args) {

        Etat etatTest = new Etat();
        Equation equation = new Equation("test", "f(x)=x+1");

        etatTest.addVariable(new Variable("x", 20));
        etatTest.getVariable("x").ajouteEquation(equation);

        Simulation sim = new Simulation("test");

        Etat nvEtat = sim.simulateStep(0,1,etatTest);
        System.out.println(etatTest);
        System.out.println(nvEtat);
        nvEtat = sim.simulateStep(1,1, nvEtat);
        System.out.println(nvEtat);
        nvEtat = sim.simulateStep(2,1, nvEtat);
        System.out.println(nvEtat);

    }
}
