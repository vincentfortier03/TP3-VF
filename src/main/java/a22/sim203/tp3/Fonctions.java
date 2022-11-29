package a22.sim203.tp3;

import javafx.scene.control.TextInputDialog;
import org.mariuszgromada.math.mxparser.Expression;
import org.mariuszgromada.math.mxparser.Function;

import java.util.Optional;

/**
 * Classe Fonctions. Cette classe permet de simplifier l'utilisation des Functions de mathparser en créant des méthodes
 * propres aux projet.
 */
public class Fonctions {
    /**
     * l'objet de type Function principal
     */
    private Function fonction;
    /**
     * String représentant la partie droite de la fonction (sans f(x)=)
     */
    private String fonctionSansFdeX;


    public Fonctions() {
        demanderStringFct();
        creerFonction();

        System.out.println(this.fonction.getFunctionExpressionString());

    }

    /**
     * Constructeur avec paramètre. Permet de créer une Fonctions sans avoir à demander à l'utilisateur
     * d'entrer celle-ci
     *
     * @param expression expression de la fonction
     */
    public Fonctions(String expression) {
        this.fonctionSansFdeX = expression;
        creerFonction();
        System.out.println(this.fonction.getFunctionExpressionString());
    }

    /**
     * méthode permettant de modifier la fonction
     */
    public void modifierFonction() {
        TextInputDialog textInputDialog = DialoguesUtils.layoutFctDialog(fonctionSansFdeX);
        Optional<String> entreeFct = textInputDialog.showAndWait();

        try {
            if (entreeFct.isPresent()) {
                this.fonctionSansFdeX = entreeFct.get();
            }
            creerFonction();
        } catch (NullPointerException e) {

        }
    }

    /**
     * Méthode créant une nouvelle Fonctions
     */
    private void creerFonction() {
        if (!validerFonctionCalculable(new Function("f(x)=" + fonctionSansFdeX))) {
            this.fonction = new Function("f(x)=" + fonctionSansFdeX);
        }
    }

    /**
     * Permet de vérifier que la fonction passée en argument est bien calculable par mathparser
     *
     * @param functionAVerifier la Fonctions à vérifier
     * @return vrai si la fonction est calculable, faux dans la négative
     */
    private boolean validerFonctionCalculable(Function functionAVerifier) {
        return Double.isNaN(functionAVerifier.calculate(5.3));
    }

    /**
     * Lance une boite de dialogue demandant une entrée à l'utilisateur. Cette entrée représente l'expression de la
     * Fonctions.
     */
    private void demanderStringFct() {
        TextInputDialog textInputDialog = DialoguesUtils.layoutFctDialog("");
        Optional<String> entreeFct = textInputDialog.showAndWait();

        try {
            if (entreeFct.isPresent()) {
                this.fonctionSansFdeX = entreeFct.get();
            }
            creerFonction();
        } catch (NullPointerException e) {

        }
    }

    /**
     * Calcule la fonction en s'assurant que l'argument et valide
     *
     * @param argument la valeur de X avec laquelle calculer la fonctions
     * @return la réponse
     */
    public double calculer(String argument) {
        double reponse = Double.NaN;

        if (!argument.isEmpty()) {
            reponse = this.fonction.calculate(Double.parseDouble(argument));
        } else DialoguesUtils.alerteCalculFct();

        return reponse;
    }

    /**
     * Converti une chaine de caractère en expression en s'assurant que la syntaxe est bonne.
     *
     * @param params la fonction sans f(x)= à convertire en expression
     * @return l'objet de type Expression
     */
    public Expression getAsExpression(String params) {
        Expression expression = null;
        if (new Expression(params).checkSyntax()) {
            expression = new Expression(params);
        }

        return expression;
    }

    /**
     * Méthode permettant de calculer la fonction en paramètre tout en retournant un String formatté de la bonne façons
     *
     * @param params le string représentant la valeur de X utilisée
     * @return le string correspondant à la "f(params) = réponse"
     */
    public String getFonctionAsString(String params) {
        String expression = "f(" + params + ") = " + this.calculer(params);

        return expression;
    }

    /**
     * Getter de fonctionSansFdeX
     *
     * @return la valeur de fonctionSansFdeX
     */
    public String getFonctionSansFDX() {
        return this.fonctionSansFdeX;
    }

    /**
     * Permet d'obtenir un string correspondant à f(x)="fonction non calculée". Sert surtout pour la ListView des fonctions
     *
     * @return un string formatté comme désiré
     */
    @Override
    public String toString() {
        return "f(x)=" + this.fonction.getFunctionExpressionString();
    }

}
