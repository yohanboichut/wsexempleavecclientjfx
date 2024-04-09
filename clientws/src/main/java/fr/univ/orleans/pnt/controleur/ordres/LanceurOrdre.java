package fr.univ.orleans.pnt.controleur.ordres;

public interface LanceurOrdre {


    void setAbonnement(EcouteurOrdre ecouteurOrdre,TypeOrdre ... typeOrdre);

    void executerOrdre(TypeOrdre typeOrdre);
}
