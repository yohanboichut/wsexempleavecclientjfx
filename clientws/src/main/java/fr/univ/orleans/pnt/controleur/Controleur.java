package fr.univ.orleans.pnt.controleur;

import fr.univ.orleans.pnt.controleur.ordres.EcouteurOrdre;
import fr.univ.orleans.pnt.controleur.ordres.LanceurOrdre;
import fr.univ.orleans.pnt.controleur.ordres.TypeOrdre;
import fr.univ.orleans.pnt.exceptions.DroitsInsuffisantsException;
import fr.univ.orleans.pnt.exceptions.MauvaisIdentifiantsException;
import fr.univ.orleans.pnt.exceptions.TokenExpireException;
import fr.univ.orleans.pnt.exceptions.UtilisateurInexistantException;
import fr.univ.orleans.pnt.modele.ProxyModele;
import fr.univ.orleans.pnt.modele.dtos.PersonneDTO;
import fr.univ.orleans.pnt.vues.GestionnaireVue;

import java.util.*;

public class Controleur implements LanceurOrdre {

    private Map<TypeOrdre,Collection<EcouteurOrdre>> abonnes;

 private String token;

 private PersonneDTO personneDTO;


     private ProxyModele proxyModele;

    public Controleur(ProxyModele proxyModele, GestionnaireVue gestionnaireVue) {
        this.proxyModele = proxyModele;
        this.abonnes = new HashMap<>();
        Arrays.stream(TypeOrdre.values()).forEach(typeOrdre ->
        this.abonnes.put(typeOrdre, new ArrayList<>()));
        gestionnaireVue.setControleur(this);
        gestionnaireVue.abonnement(this);

    }

    public String getToken() {
        return token;
    }

    public PersonneDTO getUtilisateur() {
        return personneDTO;
    }

    Collection<PersonneDTO> utilisateurs;

    public Collection<PersonneDTO> getUtilisateurs() {
        return utilisateurs;
    }

    @Override
    public void setAbonnement(EcouteurOrdre ecouteurOrdre, TypeOrdre... typeOrdre) {

        Arrays.stream(typeOrdre)
                .forEach(typeOrdre1 -> this.abonnes.get(typeOrdre1).add(ecouteurOrdre));
    }

    @Override
    public void executerOrdre(TypeOrdre typeOrdre) {
        this.abonnes.get(typeOrdre).forEach(ecouteurOrdre -> ecouteurOrdre.traiter(typeOrdre));
    }

    public void run() {
        this.executerOrdre(TypeOrdre.SHOW_APPLICATION);
    }

    public void authentification(String emailText, String passwordText) {

        try {
            token = proxyModele.login(emailText, passwordText);
            this.executerOrdre(TypeOrdre.LOAD_TOKEN);
        } catch (MauvaisIdentifiantsException e) {
            this.executerOrdre(TypeOrdre.ERROR_AUTHENTIFICATION);
        }
    }

    public void rechercheUtilisateur(String text) {

        try {
            this.personneDTO = proxyModele.getPersonneById( token, text);
            this.executerOrdre(TypeOrdre.LOAD_UTILISATEUR);
        } catch (DroitsInsuffisantsException e) {
            this.executerOrdre(TypeOrdre.ERROR_DROITS_INSUFFISANTS);
        } catch (TokenExpireException e) {
            this.executerOrdre(TypeOrdre.ERROR_TOKEN_EXPIRATION);
        } catch (UtilisateurInexistantException e) {
            this.executerOrdre(TypeOrdre.ERROR_UTILISATEUR_INEXISTANT);
        }
    }

    public void touslesprofils() {

        try {
            utilisateurs =  proxyModele.getAllPersonnes(token);
            this.executerOrdre(TypeOrdre.LOAD_UTILISATEURS);
        } catch (DroitsInsuffisantsException e) {

            this.executerOrdre(TypeOrdre.ERROR_DROITS_INSUFFISANTS);

        } catch (TokenExpireException e) {
            this.executerOrdre(TypeOrdre.ERROR_TOKEN_EXPIRATION);
        }

    }
}
