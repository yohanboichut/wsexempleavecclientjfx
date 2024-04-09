package fr.univ.orleans.pnt;

import fr.univ.orleans.pnt.controleur.Controleur;
import fr.univ.orleans.pnt.modele.ProxyModele;
import fr.univ.orleans.pnt.vues.GestionnaireVue;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        Controleur controleur =new Controleur(new ProxyModele(), new GestionnaireVue(stage));
        controleur.run();

    }

    public static void main(String[] args) {
        launch();
    }

}