package a22.sim203.tp3.simulation;

import org.mariuszgromada.math.mxparser.Argument;
import org.mariuszgromada.math.mxparser.Function;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.*;


public class Simulation implements Serializable {

    private String name;
    private List<Etat> historique = new ArrayList<>();// l'état 0 est le premier état de l'historique

    public Simulation(String name, Etat initial) {
        this.name = name;
        ajouteDansHistorique(initial);

    }

    public static void main(String[] args) {
        Etat etat = new Etat();

        Variable variableY = new Variable("y",10);
        Equation equationGravite = new Equation("y(t)","f(t,y)=(-9.8(0.1)(t^2)/2)+y");

        variableY.ajouteEquation(equationGravite);
        etat.addVariable(variableY);
        Simulation simulation = new Simulation("test",etat);
        Argument[] arguments = new Argument[2];
        arguments[0] = new Argument("t",1);
        arguments[1] = new Argument("y",100);
        System.out.println(simulation.convertEquationToFunction(equationGravite).checkSyntax());
        System.out.println(simulation.convertEquationToFunction(equationGravite).getArgument("t").getArgumentName());
        System.out.println(simulation.convertEquationToFunction(equationGravite).calculate(arguments));
        Argument[] arguments2 = new Argument[2];
        arguments2[0] = new Argument("t",2);
        arguments2[1] = new Argument("y",simulation.convertEquationToFunction(equationGravite).calculate(arguments));
        System.out.println(simulation.convertEquationToFunction(equationGravite).calculate(arguments2));
        Argument[] arguments3 = new Argument[2];
        arguments3[0] = new Argument("t",3);
        arguments3[1] = new Argument("y",simulation.convertEquationToFunction(equationGravite).calculate(arguments2));
        System.out.println(simulation.convertEquationToFunction(equationGravite).calculate(arguments3));
        Argument[] arguments4 = new Argument[2];
        arguments4[0] = new Argument("t",4);
        arguments4[1] = new Argument("y",simulation.convertEquationToFunction(equationGravite).calculate(arguments3));
        System.out.println(simulation.convertEquationToFunction(equationGravite).calculate(arguments4));
        Argument[] arguments5 = new Argument[2];
        arguments5[0] = new Argument("t",5);
        arguments5[1] = new Argument("y",simulation.convertEquationToFunction(equationGravite).calculate(arguments4));
        System.out.println(simulation.convertEquationToFunction(equationGravite).calculate(arguments5));
        System.out.println(simulation.convertEquationToFunction(equationGravite).calculate(new Argument(simulation.convertEquationToFunction(equationGravite).getArgument("t").getArgumentName(),1)));
        System.out.println(simulation.convertEquationToFunction(equationGravite).calculate(new Argument(simulation.convertEquationToFunction(equationGravite).getArgument("t").getArgumentName(),2)));

        System.out.println(etat);
        Etat etat1 = new Etat(simulation.simulateStep(0,1,etat));
        System.out.println(etat1);
        Etat etat2 = new Etat(simulation.simulateStep(1,1,etat1));
        System.out.println(etat2);
        Etat etat3 = new Etat(simulation.simulateStep(2,1,etat2));
        System.out.println(etat3);
        Etat etat4 = new Etat(simulation.simulateStep(3,1,etat3));
        System.out.println(etat4);

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

    public List<Function> getFucntionListFromVariable(Variable variable){

        return convertEquationsToFunctions(variable.getEquationsCollection());
    }

    public Etat getHistorique(int step) {
        Etat retEtat = null;

        if (step <= historique.size()) {
            retEtat = historique.get(step);
        }

        return retEtat;
    }

    public Etat getLastEtat() {
        return historique.get(historique.size()-1);
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
