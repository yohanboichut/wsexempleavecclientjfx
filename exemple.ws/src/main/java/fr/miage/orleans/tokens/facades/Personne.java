package fr.miage.orleans.tokens.facades;

public record Personne(int id,String email, String nom, String prenom, String password,Role[] roles) {
}
