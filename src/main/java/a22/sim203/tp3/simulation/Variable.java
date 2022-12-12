package a22.sim203.tp3.simulation;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Classe d'une variable
 *
 * @author Vincent Fortier
 */
public class Variable implements Serializable {

    /**
     * nom de la variables
     */
    private String name;
    /**
     * valeur de la variable
     */
    private double value;

    /**
     * map contenant les noms d'équation et leur valeur associée
     */
    private Map<String, Equation> equations = new HashMap<>();


    /**
     * Constructeur de la classe Varibale. Crée une nouvelle variable selon un nom et une valeur
     * @param name le nom de la variable
     * @param value la valeur de la variable
     */
    public Variable(String name, double value) {
        this.name = name;
        this.value = value;
    }

    /**
     * Constructueur créant une copie de la variable reçue en argument
     * @param other variable initale
     */
    public Variable(Variable other) {
        name = other.name;
        value = other.value;
        for (Equation equation : other.equations.values()) {
            equations.put(equation.getName(), new Equation(equation));
        }
    }

    /**
     * retourne le nom de la varibale
     * @return le nom de la variable
     */
    public String getName() {
        return name;
    }

    /**
     * Change le nom de la variable selon le stroing passé en argument
     * @param name le nom désiré
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * reoturne la valeur de la variable
     * @return la valeur de la variable
     */
    public double getValue() {
        return value;
    }

    /**
     * définit la valeur de la variable selon le double reçu en argument
     * @param value la valeur
     */
    public void setValue(double value) {
        this.value = value;
    }

    /**
     * Permet de déterminer si l'objet passé en argument est égal à l'instance de la variable
     * @param o l'objet à comparer
     * @return vrai s'il est pareil, faux sinon
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Variable variable = (Variable) o;
        return Double.compare(variable.value, value) == 0 && Objects.equals(name, variable.name);
    }

    /**
     * calcule le hashcode de la variable
     * @return le hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(name, value);
    }

    /**
     * retourne un string contenant les informations de la variable
     * @return le string
     */
    @Override
    public String toString() {
        return "(" + name + ", " + value + ")";
    }

    /**
     * permet d'ajouter une équation à la variable
     * @param equation équation à ajouter
     */
    public void ajouteEquation(Equation equation) {
        equations.put(equation.getName(), equation);

    }

    /**
     * retourne une map des équation de la variable selon leur nom
     * @return la map
     */
    public Map<String, Equation> getEquationsMap() {
        return equations;
    }

    /**
     * retourne une collection des équations de la variable
     * @return la collection
     */
    public Collection<Equation> getEquationsCollection() {
        return equations.values();
    }
}
