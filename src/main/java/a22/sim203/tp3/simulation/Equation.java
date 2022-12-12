package a22.sim203.tp3.simulation;

import java.io.Serializable;
import java.util.Objects;

/**
 * Classe de l'objet équation
 *
 * @author Vincent Fortier
 */
public class Equation implements Serializable {

    /**
     * le nom de l'équation
     */
    private String name;
    /**
     * l'expression de l'équation
     */
    private String expression;

    /**
     * Contructeur de la classe équation. Crée une nouvelle équation avec un nom et une expression.
     * @param name nom de l'équation
     * @param expression l'expression de l'équation
     */
    public Equation(String name, String expression) {
        this.name = name;
        this.expression = expression;
    }

    /**
     * Crée une une copie de l'équation passée en argument.
     * @param equation equation à copier
     */
    public Equation(Equation equation) {
        name = equation.getName();
        expression = equation.getExpression();
    }

    /**
     * retourne le nom de l'équation
     * @return le nom de l'équation
     */
    public String getName() {
        return name;
    }

    /**
     * Définit le nom de la simulation selon une chaîne de caractère passée en argument
     * @param name le nom à définir
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * retourne l'expression associée à l'équation
     * @return l'expression de l'équation
     */
    public String getExpression() {
        return expression;
    }

    /**
     * permet de définir l'expression de l'équation
     * @param expression l'expression à définir
     */
    public void setExpression(String expression) {
        this.expression = expression;
    }

    /**
     * Permet de savoir si l'objet passé en argument est identique à l'équation instanciée
     * @param o objet à comparer
     * @return vrai si les équation sont pareilles, faux si elles ne le sont pas
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Equation equation = (Equation) o;
        return Objects.equals(name, equation.name) && Objects.equals(expression, equation.expression);
    }

    /**
     * calcul un hashcode pour l'équation instanciée
     * @return le hashcode de l'équation
     */
    @Override
    public int hashCode() {
        return Objects.hash(name, expression);
    }

    /**
     * retourne une chaine de caractères comprenant les informations de l'équation
     * @return la chaine de caractères
     */
    @Override
    public String toString() {
        return name + "->" + expression;
    }
}
