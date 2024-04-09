package fr.miage.orleans.tokens.facades;

import java.util.Collection;
import java.util.Optional;

public interface Facade {
    int enregistrerPersonne(String email, String nom, String prenom, String motDePasse) throws EmailDejaPrisException;

    Optional<Personne> getPersonneById(int id);

    int enregistrerAdmin(String adminEmail, String adminNom, String adminPrenom, String adminMDP);

    Collection<Personne> getAll();

    Optional<Personne> getPersonneByEmail(String email);
}
