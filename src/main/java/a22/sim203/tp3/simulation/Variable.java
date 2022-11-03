package a22.sim203.tp3.simulation;

import org.mariuszgromada.math.mxparser.Argument;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;

public class Variable implements Serializable {

    private String name;
    private double value;

    private Map<String, Equation> equations = new HashMap<>();

    public Variable(String name, double value) {
        this.name = name;
        this.value = value;
    }

    public Variable(Variable other) {
        name = other.name;
        value = other.value;
        for (Equation equation : other.equations.values()) {
            equations.put(equation.getName(), new Equation(equation));
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Variable variable = (Variable) o;
        return Double.compare(variable.value, value) == 0 && Objects.equals(name, variable.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value);
    }

    @Override
    public String toString() {
        return "(" + name + ", " + value + ")";
    }

    public void ajouteEquation(Equation equation) {
        equations.put(equation.getName(), equation);
    }

    public Map<String, Equation> getEquationsMap() {
        return equations;
    }

    public Collection<Equation> getEquationsCollection() {
        return equations.values();
    }

    public void setEquations(Map<String, Equation> equations) {
        this.equations = equations;
    }
}
