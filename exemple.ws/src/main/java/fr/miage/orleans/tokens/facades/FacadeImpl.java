package fr.miage.orleans.tokens.facades;

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class FacadeImpl implements Facade {

    Map<Integer,Personne> personnesMap;

    private static int ID = 0;


    public FacadeImpl() {
        this.personnesMap = new HashMap<>();
    }


    @Override
    public int enregistrerPersonne(String email, String nom, String prenom, String motDePasse) throws EmailDejaPrisException {
        if (personnesMap.containsKey(email))
            throw new EmailDejaPrisException();
        int id = ID++;
        this.personnesMap.put(id,new Personne(id,email,nom,prenom,motDePasse,new Role[]{Role.USER}));
        return id;
    }


    @Override
    public Optional<Personne> getPersonneById(int id) {
        if (this.personnesMap.containsKey(id))
            return Optional.of(this.personnesMap.get(id));
        else
            return Optional.empty();
    }

    @Override
    public int enregistrerAdmin(String adminEmail, String adminNom, String adminPrenom, String adminMDP) {
        int id = ID++;
        this.personnesMap.put(id,new Personne(id,adminEmail,adminNom,adminPrenom,adminMDP,new Role[]{Role.ADMIN,Role.USER}));
        return id;
    }


    @Override
    public Collection<Personne> getAll() {
        return this.personnesMap.values();
    }

    @Override
    public Optional<Personne> getPersonneByEmail(String email) {
        return this.personnesMap.values().stream().filter(p -> p.email().equals(email)).findFirst();
    }
}
