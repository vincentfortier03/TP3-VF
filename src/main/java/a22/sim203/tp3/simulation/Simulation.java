package a22.sim203.tp3.simulation;

import org.mariuszgromada.math.mxparser.Argument;
import org.mariuszgromada.math.mxparser.Function;

import java.io.Serializable;
import java.util.*;

public class Simulation implements Serializable {

    private String name;
    private List<Etat> historique = new ArrayList<>();// l'état 0 est le premier état de l'historique

    public Simulation() {
//todo


    }

    public Etat simulateStep(double t, double dt, Etat etatActuel) {
        Etat nouvelEtat = new Etat(etatActuel);//msd depp copy requise
        List<Function> functionList = null;

        if(t >= 0 || dt > 0){
            for(Variable variableList : etatActuel.getVariableList()){
                if(variableList.getEquationsCollection().size() != 0){
                    for(Equation eq : variableList.getEquationsCollection()){
                        nouvelEtat.getVariable(variableList.getName()).setValue();

                    }
                    nouvelEtat.getVariable(variableList.getName()).getValue();
                }
            }
            historique.add(nouvelEtat);
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
//todo


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
        Equation equation = new Equation("test", "f(x,y,z)=x+y+z");

        etatTest.addVariable(new Variable("x", 20));
        etatTest.addVariable(new Variable("y", 8));
        etatTest.addVariable(new Variable("z", 3));
        etatTest.getVariable("x").ajouteEquation(equation);
        etatTest.getVariable("y").ajouteEquation(equation);
        etatTest.getVariable("z").ajouteEquation(equation);

        Simulation sim = new Simulation();
        System.out.println(etatTest.toString());


        System.out.println(sim.simulateStep(20,0.5,etatTest).toString());
        System.out.println(sim.simulateStep(20.5,0.5,etatTest).toString());
        System.out.println(sim.simulateStep(21,0.5,etatTest).toString());
        System.out.println(sim.simulateStep(21.5,0.5,etatTest).toString());


    }

}
