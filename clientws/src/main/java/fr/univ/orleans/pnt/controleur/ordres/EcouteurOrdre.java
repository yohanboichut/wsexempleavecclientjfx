package fr.univ.orleans.pnt.controleur.ordres;

public interface EcouteurOrdre {

    void abonnement(LanceurOrdre lanceur);

    void traiter(TypeOrdre typeOrdre);

}
