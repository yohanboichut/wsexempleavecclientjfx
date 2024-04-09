package fr.univ.orleans.pnt.vues;

import fr.univ.orleans.pnt.controleur.Controleur;
import fr.univ.orleans.pnt.controleur.ordres.EcouteurOrdre;
import fr.univ.orleans.pnt.controleur.ordres.LanceurOrdre;
import fr.univ.orleans.pnt.controleur.ordres.TypeOrdre;
import fr.univ.orleans.pnt.controleur.ordres.VueInteractive;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GestionnaireVue implements EcouteurOrdre, VueInteractive {

    Collection<VueInteractive> vuesInteractives;
    Collection<EcouteurOrdre> ecouteursOrdres ;
    Stage stage;

    ApplicationVue applicationVue;


    public GestionnaireVue(Stage stage) {
        this.stage = stage;
        this.vuesInteractives = new ArrayList<>();
        this.ecouteursOrdres = new ArrayList<>();
        applicationVue = ApplicationVue.creerVue(this);
    }

    @Override
    public void abonnement(LanceurOrdre lanceur) {

        lanceur.setAbonnement(this,TypeOrdre.SHOW_APPLICATION);
        for (EcouteurOrdre e:ecouteursOrdres){
            e.abonnement(lanceur);
        }

    }

    @Override
    public void traiter(TypeOrdre typeOrdre) {

        this.stage.setScene(this.applicationVue.getScene());
        this.stage.show();

    }



    public void ajouterVue(VueInteractive vue){
        vuesInteractives.add(vue);
    }



    public void ajouterEcouteur(EcouteurOrdre ecouteur){
        ecouteursOrdres.add(ecouteur);
    }

    @Override
    public void setControleur(Controleur controleur) {
        for (VueInteractive v: vuesInteractives){
            v.setControleur(controleur);
        }

    }
}
