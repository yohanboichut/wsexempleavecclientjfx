package fr.miage.orleans.tokens.controleur;

import fr.miage.orleans.tokens.facades.EmailDejaPrisException;
import fr.miage.orleans.tokens.facades.Facade;
import fr.miage.orleans.tokens.facades.Personne;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@EnableWebSecurity
@RequestMapping("/api")
public class Controleur {

    private static final String TOKEN_PREFIX="Bearer ";
    Facade facade;
    PasswordEncoder passwordEncoder;

    Function<Personne,String> genereToken;
    public Controleur(Facade facade, PasswordEncoder passwordEncoder, Function<Personne,String> genereToken) {
        this.facade = facade;
        this.passwordEncoder = passwordEncoder;
        this.genereToken=genereToken;
    }





    @PostMapping("/register")
    public ResponseEntity<String> enregistrer(@RequestParam String email, @RequestParam String nom, @RequestParam String prenom, @RequestParam String password){
        try {
            int id=facade.enregistrerPersonne(email, nom, prenom, passwordEncoder.encode(password));
            Personne j = facade.getPersonneById(id).get();
            return ResponseEntity.status(201).header(HttpHeaders.AUTHORIZATION, TOKEN_PREFIX+genereToken.apply(j)).build();
        }
        catch (EmailDejaPrisException e) {
            return ResponseEntity.status(409).build();
        }
    }


    @PostMapping("/login")
    public ResponseEntity login( @RequestBody LoginDTO personne) {
        Optional<Personne> oj = null;

        oj = facade.getPersonneByEmail(personne.email());

        if (oj.isEmpty())
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        Personne j = oj.get();
        if (passwordEncoder.matches(personne.password(), j.password())) {
            String token = genereToken.apply(j);
            return ResponseEntity.status(201).header(HttpHeaders.AUTHORIZATION,TOKEN_PREFIX+token).build();
        };
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }





    @GetMapping("/users")
    public ResponseEntity<Collection<PersonneDTO>> getAll(){

        return ResponseEntity.ok(facade.getAll().stream().map(j->
        {String [] roles = new String[j.roles().length];
            for (int i=0;i<j.roles().length;i++)
                roles[i]=j.roles()[i].name();
            return new  PersonneDTO(j.id(),j.email(),j.nom(),j.prenom(),roles);
        }).collect(Collectors.toList()));


    }


    @GetMapping("/users/{id}")

    public ResponseEntity<PersonneDTO> getById(@PathVariable int id, JwtAuthenticationToken token){

        int idToken= Integer.valueOf((String)token.getTokenAttributes().get("id"));

        if (idToken!=id && !token.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        Optional<Personne> oj = facade.getPersonneById(id);
        if (oj.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        Personne j = oj.get();
        String [] roles = new String[j.roles().length];
        for (int i=0;i<j.roles().length;i++)
            roles[i]=j.roles()[i].name();

        return ResponseEntity.ok(new PersonneDTO(j.id(),j.email(),j.nom(),j.prenom(),roles));
    }




}
