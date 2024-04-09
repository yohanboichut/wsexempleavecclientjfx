package fr.univ.orleans.pnt.vues;

import fr.univ.orleans.pnt.App;
import fr.univ.orleans.pnt.controleur.Controleur;
import fr.univ.orleans.pnt.controleur.ordres.*;
import fr.univ.orleans.pnt.modele.dtos.PersonneDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

import java.io.IOException;

import static fr.univ.orleans.pnt.controleur.ordres.TypeOrdre.ERROR_DROITS_INSUFFISANTS;

public class ApplicationVue implements EcouteurOrdre, VueInteractive, Vue {



    @FXML
    BorderPane borderPane;




    @FXML
    TextArea resultatTouslesProfils;

    @FXML
    TextField idRecherche;


    @FXML
    TextField token;


    @FXML
    TextField email;


    @FXML
    PasswordField password;

    private Scene scene;
    private Controleur controleur;

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public static ApplicationVue creerVue(GestionnaireVue gestionnaireVue) {
        FXMLLoader fxmlLoader = new FXMLLoader(ApplicationVue.class.getResource("application.fxml"));

        try {
            fxmlLoader.load();
            ApplicationVue vue = fxmlLoader.getController();
            gestionnaireVue.ajouterVue(vue);
            gestionnaireVue.ajouterEcouteur(vue);
            vue.scene = new Scene(vue.borderPane, 800, 600);
            return vue;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void abonnement(LanceurOrdre lanceur) {
        lanceur.setAbonnement(this,
                TypeOrdre.LOAD_TOKEN,
                TypeOrdre.LOAD_UTILISATEURS,
                TypeOrdre.LOAD_UTILISATEUR,
                TypeOrdre.ERROR_UTILISATEUR_INEXISTANT,
                TypeOrdre.ERROR_TOKEN_EXPIRATION,
                TypeOrdre.ERROR_AUTHENTIFICATION,
                ERROR_DROITS_INSUFFISANTS
                );

    }


    private void erreur(String message){
        Alert alert = new Alert(Alert.AlertType.ERROR,message, ButtonType.OK);
        alert.showAndWait();

    }
    @Override
    public void traiter(TypeOrdre typeOrdre) {

        switch (typeOrdre)
        {
            case LOAD_TOKEN -> {
                token.setDisable(true);
                token.setText(controleur.getToken());
            }

            case LOAD_UTILISATEUR -> {
                PersonneDTO p = controleur.getUtilisateur();
                this.resultatTouslesProfils.setText(p.id()+" - "+p.nom()+" "+p.prenom()+"\n email : "
                        +p.email()+"\n roles: "+String.join(", ", p.roles()));
            }

            case LOAD_UTILISATEURS -> {
                this.resultatTouslesProfils.setText("");
                controleur.getUtilisateurs().stream().map(x -> x.id()+" - "+x.nom()+" "+x.prenom()+" "+x.email()+"\n").forEach(this.resultatTouslesProfils::appendText);

            }

            case ERROR_DROITS_INSUFFISANTS -> {
                erreur("Droits insuffisants");
            }

            case ERROR_AUTHENTIFICATION -> {
                erreur("Authentification échouée");
            }

            case ERROR_UTILISATEUR_INEXISTANT ->
                erreur("Utilisateur inexistant");


            case ERROR_TOKEN_EXPIRATION ->
                erreur("Token expiré, vous devez le recharger !");
        }



    }

    @Override
    public void setControleur(Controleur controleur) {
        this.controleur = controleur;
    }

    @Override
    public Scene getScene() {
        return scene;
    }

    public void authentification(ActionEvent actionEvent) {

        controleur.authentification(email.getText(), password.getText());
    }

    public void rechercheUtilisateur(ActionEvent actionEvent) {

            controleur.rechercheUtilisateur(idRecherche.getText());
    }

    public void touslesprofils(ActionEvent actionEvent) {

        controleur.touslesprofils();
    }
}
