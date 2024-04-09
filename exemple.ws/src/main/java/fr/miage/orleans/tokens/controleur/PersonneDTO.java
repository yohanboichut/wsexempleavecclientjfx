package fr.miage.orleans.tokens.controleur;

import fr.miage.orleans.tokens.facades.Role;

public record PersonneDTO(int id,String email, String nom, String prenom,  String[] roles) {
}
