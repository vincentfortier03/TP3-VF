package a22.sim203.tp3.controlleurs;

import a22.sim203.tp3.DialoguesUtils;
import a22.sim203.tp3.Fonctions;
import a22.sim203.tp3.SimulationApp;
import a22.sim203.tp3.simulation.Equation;
import a22.sim203.tp3.simulation.Simulation;
import a22.sim203.tp3.simulation.Variable;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.mariuszgromada.math.mxparser.Expression;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * Classe controller pour le fichier FXML Calculatrice.fxml
 *
 * @author Vincent Fortier
 */
public class CalculatriceController implements Initializable {
    @FXML
    private Button bouttonMr;
    @FXML
    private CheckMenuItem checkMenuChangement;
    @FXML
    private Label labelAffichage;
    /**
     * Liste des fonctions
     */
    @FXML
    private ListView<String> listViewFonctions;
    /**
     * Affichage de l'historique des calculs
     */
    @FXML
    private ListView<String> listViewHistorique;
    /**
     * Gestionaire de disposition "BorderPane"
     * Racine de la scène.
     */
    @FXML
    private BorderPane root;
    /**
     * Liste simple stockant les objets de type "Fonctions"
     * Permettra d'accéder les Fonctions facilement
     */
    private List<Fonctions> listFct;
    /**
     * Map contenant l'historique des calculs.
     * La clée est une chaine de charactère représentant l'équation qui sera affichée dans la ListView d'historique.
     * La valeur associée aux clées sont des objets de type Expressions de MathParser
     */
    private Map<String, Expression> mapHistorique;
    /**
     * Même principe que mapHistorique, mais pour la ListView des Fonctions.
     * La valeur associée à la clée est plutôt une Fonctions
     */
    private Map<String, Fonctions> mapFonctions;
    /**
     * la listView de s simulations
     */
    @FXML
    private ListView<Simulation> listViewSimulations;
    /**
     * la listView des équations
     */
    @FXML
    private ListView<Equation> listViewEquations;
    /**
     * la listView des variables
     */
    @FXML
    private ListView<Variable> listViewVariables;

    /**
     * Méthode redéfinie de Initializable. Initialise le controlleur.
     *
     * @param location  emplacement
     * @param resources resource
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.listFct = new ArrayList<>();
        this.mapHistorique = new HashMap<>();
        this.mapFonctions = new HashMap<>();

        ajouterFctManuellement(new Fonctions("x"));
        ajouterFctManuellement(new Fonctions("sqrt(x)"));
        ajouterFctManuellement(new Fonctions("sin(x)"));
        ajouterFctManuellement(new Fonctions("cos(x)"));
        ajouterFctManuellement(new Fonctions("x^2"));

        listViewHistorique.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> doubleCliqueHistorique(e));
        listViewFonctions.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> doubleCliqueFct(e));


        listViewSimulations.setCellFactory((e) -> new simCell());
        listViewVariables.setCellFactory((e) -> new varCell());
        listViewEquations.setCellFactory((e) -> new eqCell());

        listViewSimulations.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<Simulation>() {
            @Override
            public void onChanged(Change<? extends Simulation> c) {
                refreshVar();
            }
        });
        listViewVariables.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<Variable>() {
            @Override
            public void onChanged(Change<? extends Variable> c) {
                refreshEqu();
            }
        });


    }


    /**
     * permet de retirer de la listView l'équation sélectionnée
     * @param event clique de l'option retirer du menu contextuel
     */
    @FXML
    void retirerEquation(ActionEvent event) {
        if (!listViewEquations.getSelectionModel().isEmpty()) {
            listViewVariables.getSelectionModel().getSelectedItem().getEquationsCollection().remove(listViewEquations.getSelectionModel().getSelectedItem());
            refreshEqu();
        }
    }
    /**
     * permet de retirer de la listView la simulation sélectionnée
     * @param event clique de l'option retirer du menu contextuel
     */
    @FXML
    void retirerSimulation(ActionEvent event) {
        listViewSimulations.getItems().remove(listViewSimulations.getSelectionModel().getSelectedItem());
        listViewVariables.getItems().clear();
        refreshVar();
        refreshEqu();
    }
    /**
     * permet de retirer de la listView la variable sélectionnée
     * @param event clique de l'option retirer du menu contextuel
     */
    @FXML
    void retirerVariable(ActionEvent event) {
        if (!listViewSimulations.getSelectionModel().isEmpty()) {
            listViewSimulations.getSelectionModel().getSelectedItem().getPlusRecentEtat().getVariableList().remove(listViewVariables.getSelectionModel().getSelectedItem());
            refreshVar();
        }
    }


    /**
     * permet d'ajouter une simulation selon les informations recueillies dans un dialogue à cet égard
     * @param actionEvent le clique du boutton ajouter du menu contextuel
     * @throws IOException une exception lancée par le dialogue
     */
    @FXML
    public void ajouterSimulation(ActionEvent actionEvent) throws IOException {
        Simulation simulationSaisie = DialoguesUtils.dialogSimulation(false, null);

        if (simulationSaisie != null) {
            listViewSimulations.getItems().add(simulationSaisie);
        }

        listViewSimulations.refresh();
        listViewVariables.refresh();
    }
    /**
     * permet d'ajouter une équation selon les informations recueillies dans un dialogue à cet égard
     * @param event le clique du boutton ajouter du menu contextuel
     * @throws IOException une exception lancée par le dialogue
     */
    @FXML
    void ajouterEquation(ActionEvent event) throws IOException {
        Equation equationSaisie = DialoguesUtils.dialogueEquation(null);

        if (equationSaisie != null) {
            listViewVariables.getSelectionModel().getSelectedItem().ajouteEquation(equationSaisie);
        }
        refreshEqu();
    }
    /**
     * permet d'ajouter une variable selon les informations recueillies dans un dialogue à cet égard
     * @param event le clique du boutton ajouter du menu contextuel
     * @throws IOException une exception lancée par le dialogue
     */
    @FXML
    void ajouterVariable(ActionEvent event) throws IOException {
        Variable variableSaisie = DialoguesUtils.dialogueVariable(false, null);

        if (variableSaisie != null) {
            listViewSimulations.getSelectionModel().getSelectedItem().getPlusRecentEtat().addVariable(variableSaisie);
        }
        refreshVar();
    }


    /**
     * permet de modifier la simulation sélectionnée selon les informations recueillies dans un dialogue à cet égard
     * @param event le clique du boutton modifier du menu contextuel
     * @throws IOException une exception lancée par le dialogue
     */
    @FXML
    void modifierSimulation(ActionEvent event) throws IOException {
        if (!listViewSimulations.getSelectionModel().isEmpty()) {
            DialoguesUtils.dialogSimulation(true, listViewSimulations.getSelectionModel().getSelectedItem());
        }
        listViewSimulations.refresh();
    }
    /**
     * permet de modifier la variable sélectionnée selon les informations recueillies dans un dialogue à cet égard
     * @param event le clique du boutton modifier du menu contextuel
     * @throws IOException une exception lancée par le dialogue
     */
    @FXML
    void modifierVariable(ActionEvent event) throws IOException {
        if (!listViewVariables.getSelectionModel().isEmpty()) {
            DialoguesUtils.dialogueVariable(true, listViewVariables.getSelectionModel().getSelectedItem());
        }
        refreshVar();
    }
    /**
     * permet de modifier l'équation sélectionnée selon les informations recueillies dans un dialogue à cet égard
     * @param event le clique du boutton modifier du menu contextuel
     * @throws IOException une exception lancée par le dialogue
     */
    @FXML
    void modifierEquation(ActionEvent event) throws IOException {
        if (!listViewEquations.getSelectionModel().isEmpty()) {
            DialoguesUtils.dialogueEquation(listViewEquations.getSelectionModel().getSelectedItem());
        }
        listViewEquations.refresh();
    }

    /**
     * permet de charger une simulation selon un fichier .sim choisi dans un dialogue de sélectiond de fichier
     * @param event le clique du boutton charger
     * @throws IOException une exception lancée par le dialogue
     */
    @FXML
    void charger(ActionEvent event) throws IOException {
        Simulation simulationChargee = DialoguesUtils.openFileDialog(root.getScene().getWindow());

        if (simulationChargee != null) {
            listViewSimulations.getItems().add(simulationChargee);
        }
    }

    /**
     * Lance la simulation dans une nouvelle fenêtre selon l'équation sélectionnée dans le listView des équations
     * @param event le clique du boutton simuler
     * @throws IOException exception lancée par la méthode load
     */
    @FXML
    void lancerSimulation(ActionEvent event) throws IOException {
        Stage simStage = new Stage();
        FXMLLoader load = new FXMLLoader(SimulationApp.class.getResource("Simulateur.fxml"));
        Parent root = load.load();
        SimulateurController controller = load.getController();

        if (!listViewEquations.getSelectionModel().isEmpty()) {
            controller.setSimulationActuelle(listViewSimulations.getSelectionModel().getSelectedItem());
            controller.setEquationSelectionnee(listViewEquations.getSelectionModel().getSelectedItem());

            simStage.setScene(new Scene(root));
            simStage.show();
        } else {
            System.out.println("Aucune simulation sélectionnée");
        }
    }

    /**
     * permet de sauvegarder la simulation sélectionnée dans un fichier .sim à un emplacement choisi par un dialogue
     * de sélection de fichier.
     * @param event le clique du boutton sauvegarder
     * @throws IOException une exception lancée par le dialogue de sélection de fichier
     */
    @FXML
    void save(ActionEvent event) throws IOException {
        if (!listViewSimulations.getSelectionModel().isEmpty()) {
            DialoguesUtils.saveFileDialog(root.getScene().getWindow(), listViewSimulations.getSelectionModel().getSelectedItem());
            System.out.println("saved");
        }
    }

    /**
     * permet de mettre à jour la listeView des équation
     */
    private void refreshEqu() {
        listViewEquations.getItems().clear();
        if (!listViewVariables.getSelectionModel().isEmpty()) {
            listViewEquations.getItems().addAll(listViewVariables.getSelectionModel().getSelectedItem().getEquationsCollection());
        }
        listViewEquations.refresh();

    }

    /**
     * permet de mettre à jour la listView des variables
     */
    private void refreshVar() {
        listViewVariables.getItems().clear();
        refreshEqu();
        if (!listViewSimulations.getSelectionModel().isEmpty()) {
            listViewVariables.getItems().addAll(listViewSimulations.getSelectionModel().getSelectedItem().getPlusRecentEtat().getVariableList());
        }
        listViewVariables.refresh();
    }

    /**
     * Méthode s'occupant de la fonctionnalité des doubles-clics sur les items de la ListView fonctions.
     *
     * @param e évenement déclanchant la méthode
     */
    private void doubleCliqueFct(MouseEvent e) {
        Fonctions fctActuelle;
        // On s'assure qu'un élément de la ListView des fonctions est bel et bien sélectionné
        if (listViewFonctions.getSelectionModel().getSelectedIndex() >= 0) {
            fctActuelle = this.listFct.get(listViewFonctions.getSelectionModel().getSelectedIndices().get(0));
            //Appel la méthode checkDoubleClick pour vérifier que l'évènement est un double-click
            if (checkDoubleClick(e)) {
                //On s'assure que l'affichage n'est pas vide/null
                if (!labelAffichage.getText().isEmpty()) {
                    //Pour s'assurer qu'aucune erreur empêche le bon fonctionnement
                    try {
                        addToHistoriqueFonctions(fctActuelle, this.labelAffichage.getText());
                        labelAffichage.setText("" + fctActuelle.calculer(labelAffichage.getText()));
                    } catch (Exception exception) {
                        //Si une exception est lancée, une alerte est lancée
                        DialoguesUtils.alerteCalculFct();
                    }
                } else {
                    DialoguesUtils.alerteCalculFct();
                    e.consume();
                }
            }
        }
    }

    /**
     * Méthode s'occupant de la fonctionnalité des doubles-clics sur les items de l'historique.
     *
     * @param e évenement déclanchant la méthode
     */
    private void doubleCliqueHistorique(MouseEvent e) {
        // On s'assure qu'un élément de l'historique est bel et bien sélectionné
        if (listViewFonctions.getSelectionModel().getSelectedIndex() >= 0) {
            if (checkDoubleClick(e)) {
                //Puisque les Expressions et les Fonctions ont un fonctionnement différent, on les trie.
                //Le premier if statement permet de vérifier si l'item sélectionné est une fonction en regardant le
                //premier caractère. Puisqu'aucune expression peut commencer par F, on choisie f comme caractère.
                // (Avec mathparser il est possible de changer f(x) pour g(x) par exemple mais c'est impossible
                //  à faire pour l'utilisateur dans notre situation)
                if (this.listViewHistorique.getSelectionModel().getSelectedItem().charAt(0) == 'f') {
                    //On va chercher la valeur de X de l'historique en faisant un substring d'entre les paranthèses
                    String stringExpression = listViewHistorique.getSelectionModel().getSelectedItem();
                    stringExpression = stringExpression.substring(stringExpression.indexOf("(") + 1);
                    stringExpression = stringExpression.substring(0, stringExpression.indexOf(")"));
                    labelAffichage.setText(stringExpression);
                } else {
                    //Si c'est une expression simple
                    labelAffichage.setText(mapHistorique.get(listViewHistorique.getSelectionModel().getSelectedItem()).getExpressionString());
                }
            }
        }
    }


    /**
     * Méthode permettant de vérifier si l'évènement reçu en paramètre est un double clique
     *
     * @param event évènement de souris à être évalué
     * @return Oui si c'est un double-clic,sinon, false
     */
    private boolean checkDoubleClick(MouseEvent event) {
        boolean isDoubleClick = false;
        //Utilise la méthode des MouseEvent retournant le nombre de clics
        if ((event).getClickCount() == 2) {
            isDoubleClick = true;
        }
        return isDoubleClick;
    }

    /**
     * Méthode permettant d'ajouter à l'affichage le texte que la touche cliquée représente
     *
     * @param event touche déclanchant la méthode
     */
    @FXML
    void ajouteKey(KeyEvent event) {
        this.labelAffichage.setText(this.labelAffichage.getText() + event.getText());
        event.consume();
    }

    /**
     * Méthode permettant de filtrer les touches appuyées pour leur associer les bons comportements.
     *
     * @param event touche déclanchant la méthode
     */
    @FXML
    void keyInput(KeyEvent event) {
    /*  Filtre personnalisé pour vérifier si la touche appuyée est un chiffre ou la touche retour. Vérifie
        chaque touche qui est appuyée, pas seulement celles qui sont "enclenchées" car le KeyCode d'une touche
        "Typed" est toujours KeyCode.UNDEFINED. Ça rend la tâche de trouver un "KeyCode.BACK_SPACE" plus compliqué. */

        /*  Vérifie en premier si la touche enfoncée est la touche retour.  */
        if (event.getCode().equals(KeyCode.BACK_SPACE)) {
            retour(new ActionEvent());

    /*  Si ce n'est pas la touche retour, on vérifie à l'aide d'un REGEX (',', '(', ')', '+', '-' ou de 0 à 9) que
        le texte associé à la touche enfoncée est bel et bien une action possible.  */
        } else if (event.getText().matches("[*.()+-[0-9]]")) {
            ajouteKey(event);

    /*  Si ce n'est pas une touche de caractère valide, on vérifie si c'est la touche égale. Dans la positive,
        on appelle la même méthode utilisée quand le boutton égal est enfoncé en lui passant un ActionEvent anonyme  */
        } else if (event.getCode().equals(KeyCode.EQUALS)) {
            calculerMethode(new ActionEvent());

            /*  Si ce n'est aucun des cas ci-haut, la touche est inutile au programme et l'évènement est détruit (ou consumé)*/
        } else event.consume();
    }

    /**
     * Méthode permettant de calculer, d'ajouter à l'historique et d'afficher le résultat d'une expression.
     *
     * @param event évènement déclanchant la méthode.
     */
    @FXML
    void calculerMethode(ActionEvent event) {
        Expression expression = new Expression(this.labelAffichage.getText());

        //On s'assure que l'expression est calculable
        if (!Double.isNaN(expression.calculate())) {
            //on l'ajoute à l'historique
            addToHistoriqueExpression(expression);
            //On l'affiche
            this.labelAffichage.setText(Double.toString(expression.calculate()));
            //Si l'expression n'est pas calculable, on lance l'alerte adéquate.
        } else DialoguesUtils.alerteNaN();
        event.consume();
    }

    /**
     * Ajoute l'expression passé en argument à l'historique
     *
     * @param expression Expression à ajouter à l'historique
     */
    private void addToHistoriqueExpression(Expression expression) {
        String expressionCalculee = expression.getExpressionString() + " = " + expression.calculate();
        listViewHistorique.getItems().add(expressionCalculee);
        mapHistorique.put(expressionCalculee, expression);
    }

    /**
     * Ajoute la fonction passée en argument à l'historique en la calculant à l'aide du string aussi passé en argument
     *
     * @param fonctions Expression à ajouter à l'historique
     * @param str       chaine de caractère représentant la valeur de X à utiliser pour le calcul
     */
    private void addToHistoriqueFonctions(Fonctions fonctions, String str) {
        mapHistorique.put(fonctions.getFonctionAsString(str), fonctions.getAsExpression(fonctions.getFonctionSansFDX()));
        listViewHistorique.getItems().add(fonctions.getFonctionAsString(str));
    }

    /**
     * Enlève un caractère à l'affichage à l'aide d'un substring. S'assure avant que la longueur n'est pas 0.
     *
     * @param event évènenement déclanchant la méthode
     */
    @FXML
    void retour(ActionEvent event) {
        if (this.labelAffichage.getText().length() != 0) {
            this.labelAffichage.setText(this.labelAffichage.getText().substring(0, labelAffichage.getText().length() - 1));
        } else event.consume();
    }

    /**
     * Permet de vider l'affichage de la calculatrice.
     *
     * @param event évènement déclanchant la méthode
     */
    @FXML
    void clearAffichage(ActionEvent event) {
        this.labelAffichage.setText("");
        event.consume();
    }

    /**
     * Ajoute au UserData du bouton mémoire ce qui est dans l'affichage
     *
     * @param event évènement déclanchant la méthode
     */
    @FXML
    void toMemoire(ActionEvent event) {
        bouttonMr.setUserData(this.labelAffichage.getText());
        event.consume();
    }

    /**
     * Ajoute à l'affichage le contenu du UserData du boutton target à l'événement reçu en argument
     *
     * @param event
     */
    @FXML
    void ajouterAffichageUD(ActionEvent event) {
        ajouterALAffichage(((Button) (event.getSource())).getUserData().toString());
        event.consume();
    }

    /**
     * méthode créant une nouvelle fonction et en l'ajoutant à la listView des fonctions
     *
     * @param event évènement déclanchant la méthode
     */
    @FXML
    void ajouterFct(ActionEvent event) {
        Fonctions fct = null;
        try {
            fct = new Fonctions();
            this.listViewFonctions.getItems().add(fct.toString());
        } catch (NullPointerException e) {
            event.consume();
        }
        if (fct != null) {
            listFct.add(fct);
            mapFonctions.put(fct.toString(), fct);
            event.consume();
        }

        event.consume();
    }

    /**
     * Méthode permettant d'ajouter manuellement une fonction à la liste.
     * Méthode inutile pour l'utilisateur, seulement là pour la propretée
     *
     * @param fct fonction à ajouter à la liste
     */
    private void ajouterFctManuellement(Fonctions fct) {
        this.listViewFonctions.getItems().add(fct.toString());
        this.listFct.add(fct);
        mapFonctions.put(fct.toString(), fct);
    }

    /**
     * permet d'ajouter le string reçu en paramètres à l'affichage
     *
     * @param text String à ajouter à l'affichage
     */
    private void ajouterALAffichage(String text) {
        this.labelAffichage.setText(this.labelAffichage.getText() + text);
    }


    /**
     * S'occupe de l'effacement de la fonction sélectionnée par le menu contextuel
     *
     * @param event évènement déclanchant la méthode
     */
    @FXML
    void effacerFct(ActionEvent event) {
        int index = listViewFonctions.getSelectionModel().getSelectedIndex();
        if (index >= 0) {
            listViewFonctions.getItems().remove(index);
            mapFonctions.remove(listViewFonctions.getSelectionModel().getSelectedItem());
        }
        event.consume();
    }

    /**
     * S'occupe de la modification de la fonction sélectionnée par le menu contextuel
     *
     * @param event évènement déclanchant la méthode
     */
    @FXML
    void modifierFct(ActionEvent event) {
        int index = listViewFonctions.getSelectionModel().getSelectedIndex();
        if (index >= 0) {
            listFct.get(index).modifierFonction();
            listViewFonctions.getItems().set(index, listFct.get(index).toString());
        }
        event.consume();
    }

    /**
     * Méthode s'occupant de toutes les fonctionnalités des boutons de fonctions.
     *
     * @param event évènement déclanchant la méthode
     */
    @FXML
    void fonctionaliteNoFX(ActionEvent event) {
        Button button = ((Button) event.getSource());
        Fonctions fctUserData = ((Fonctions) button.getUserData());
        //S'assure que la case de changement est cochée et qu'une fonction est belle et bien sélectionnée
        if (checkMenuChangement.isSelected() && listViewFonctions.getSelectionModel().getSelectedIndex() >= 0) {
            button.setUserData(mapFonctions.get(listViewFonctions.getSelectionModel().getSelectedItem()));
            button.setText(listViewFonctions.getSelectionModel().getSelectedItem());
            event.consume();
            //Si la case n'est pas cochée, on vérifie que le boutton contient une fonction.
        } else if (!button.getText().equals("noFX")) {
            if (!labelAffichage.getText().isEmpty() && labelAffichage.getText() != null) {
                addToHistoriqueFonctions(fctUserData, this.labelAffichage.getText());
                labelAffichage.setText("" + fctUserData.calculer(labelAffichage.getText()));
                event.consume();
            } else DialoguesUtils.alerteCalculFct();
            event.consume();
        } else event.consume();
    }

    /**
     * Affiche l'alerte à propos du menu contextuel.
     *
     * @param event évènement déclanchant la méthode
     */
    @FXML
    void afficherAPropos(ActionEvent event) {
        DialoguesUtils.alerteAide();
        event.consume();
    }


    /**
     * Classe innterne SimCell
     */
    static class simCell extends ListCell<Simulation> {
        /**
         * le string à afficher dans la cellule
         */
        private String stringAAfficher;
        /**
         * le hbox de la cellule
         */
        private HBox hBox;
        /**
         * le label dans lequel ira le stringAAfficher
         */
        private Label label;

        /**
         * constructeur de la classe simCell
         */
        public simCell() {
            label = new Label();
            hBox = new HBox(label);
        }

        /**
         * met à jour la valeur et l'affichage de la cellule
         * @param sim   The new item for the cell.
         * @param empty whether or not this cell represents data from the list. If it
         *              is empty, then it does not represent any domain data, but is a cell
         *              being used to render an "empty" row.
         */
        @Override
        public void updateItem(Simulation sim, boolean empty) {
            super.updateItem(sim, empty);

            if (sim == null || empty) {
                setItem(null);
                setGraphic(null);
            } else {
                stringAAfficher = sim.getName();
                setItem(sim);
                label.setText(stringAAfficher);
                setGraphic(hBox);
            }
        }
    }
    /**
     * Classe interne varCell
     */
    static class varCell extends ListCell<Variable> {

        private String stringAAfficher;
        /**
         * le hbox de la cellule
         */
        private HBox hBox;
        /**
         * le label dans lequel ira le stringAAfficher
         */
        private Label label;

        /**
         * contructeur de la classe varCell
         */
        public varCell() {
            label = new Label();
            hBox = new HBox(label);
        }

        /**
         * met à jour la valeur et l'affichage de la cellule
         * @param variable The new item for the cell.
         * @param empty    whether or not this cell represents data from the list. If it
         *                 is empty, then it does not represent any domain data, but is a cell
         *                 being used to render an "empty" row.
         */
        @Override
        public void updateItem(Variable variable, boolean empty) {
            super.updateItem(variable, empty);

            if (variable == null || empty) {
                setItem(null);
                setGraphic(null);
            } else {
                stringAAfficher = variable.getName();
                setItem(variable);
                label.setText(stringAAfficher + " = " + variable.getValue());
                setGraphic(hBox);
            }
        }
    }
    /**
     * Classe interne varCell
     */
    static class eqCell extends ListCell<Equation> {
        /**
         * le string à afficher dans la cellule
         */
        private String stringAAfficher;
        /**
         * le hbox de la cellule
         */
        private HBox hBox;
        /**
         * le label dans lequel ira le stringAAfficher
         */
        private Label label;

        /**
         * contructeur de la classe eqCell
         */
        public eqCell() {
            label = new Label();
            hBox = new HBox(label);
        }

        /**
         * met à jour la valeur et l'affichage de la cellule
         * @param equation The new item for the cell.
         * @param empty    whether or not this cell represents data from the list. If it
         *                 is empty, then it does not represent any domain data, but is a cell
         *                 being used to render an "empty" row.
         */
        @Override
        public void updateItem(Equation equation, boolean empty) {
            super.updateItem(equation, empty);

            if (equation == null || empty) {
                setItem(null);
                setGraphic(null);
            } else {
                stringAAfficher = equation.getExpression();
                setItem(equation);
                label.setText(stringAAfficher);
                setGraphic(hBox);
            }
        }
    }
}
