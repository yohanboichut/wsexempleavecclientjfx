package fr.univ.orleans.pnt.modele;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.databind.type.CollectionType;
import fr.univ.orleans.pnt.exceptions.*;
import fr.univ.orleans.pnt.modele.dtos.LoginDTO;
import fr.univ.orleans.pnt.modele.dtos.PersonneDTO;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collection;
import java.util.Optional;

public class ProxyModele {

    // Status de retour
    private static final int STATUS_OK = 200;
    private static final int STATUS_CREATED = 201;
    private static final int STATUS_BAD_REQUEST = 400;
    private static final int STATUS_UNAUTHORIZED = 401;

    private static final int STATUS_NOT_FOUND = 404;
    private static final int STATUS_NOT_ACCEPTABLE = 406;
    private static final int STATUS_CONFLICT = 409;

    // URIs
    private static final String URI_WEB_SERVICE = "http://localhost:8080/api/";
    private static final String URI_USERS = URI_WEB_SERVICE + "users";
    private static final String URI_LOGI = URI_WEB_SERVICE + "login";



    // Entêtes des requêtes ou réponses
    private static final String ENTETE_CONTENT_TYPE = "Content-Type";
    private static final String ENTETE_ACCEPT = "Accept";
    private static final String CONTENT_TYPE_APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";
    private static final String CONTENT_TYPE_APPLICATION_JSON = "application/json";
    private static final String ENTETE_TOKEN = "Authorization";

    // Données dans le corps des requêtes
    private static final String FORMAT_PARAM = "{0}={1}";
    private static final String PARAM_PROPOSITION = "proposition";
    private static final String PARAM_PSEUDO = "pseudo";

    // Messages d'erreur
    private static final String ERREUR_TOKEN_MANQUANT = "Token expiré";
    private static final String ERREUR_TOKEN_PARTIE_MANQUANT = "Token de partie manquant dans l'entête de la réponse";
    private static final String ERREUR_STATUS_INATTENDU = "Code status {0} inattendu";
    private static final int STATUS_FORBIDDEN = 403;

    private final HttpClient httpClient = HttpClient.newHttpClient();;
    private ObjectMapper objectMapper=new ObjectMapper();




    public String login(String pseudo, String motDePasse)  throws MauvaisIdentifiantsException {
        HttpRequest request = null;
        try {
            request = HttpRequest.newBuilder()
                    .uri(URI.create(URI_LOGI))
                    .setHeader("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(new LoginDTO(pseudo, motDePasse))))
                    .build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // 1. Envoi de la requête, et récupération de la réponse
        HttpResponse<String> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new ErreurInattendueException("Erreur lors de l'envoi de la requête : " + e.getMessage());
        }

        // 1. Traitement de la réponse, selon le statut reçu
        int statusCode = response.statusCode();
        switch (statusCode) {
            case 201: {
                Optional<String> token = response.headers().firstValue("Authorization");
                if (token.isPresent()) {
                    return token.get();
                } else {
                    throw new ErreurInattendueException("Token manquant dans la réponse !");
                }
            }
            case 401: {
                throw new MauvaisIdentifiantsException();
            }
            default:
                throw new ErreurInattendueException("Status inattendu : " + statusCode);
        }
    }



    public Collection<PersonneDTO> getAllPersonnes(String token) throws DroitsInsuffisantsException, TokenExpireException {

        HttpRequest request = null;
        try {
            request = HttpRequest.newBuilder()
                    .uri(URI.create(URI_USERS))
                    .setHeader(ENTETE_ACCEPT, CONTENT_TYPE_APPLICATION_JSON)
                    .setHeader(ENTETE_TOKEN, token)
                    .build();
        } catch (Exception e) {
            throw new ErreurInattendueException("Erreur lors de l'envoi de la requête : " + e.getMessage());
        }
        HttpResponse<String> response;
        try {
            response=httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            switch (response.statusCode()) {
                case STATUS_OK: {
                    String body = response.body();
                    CollectionType collectionType =objectMapper.getTypeFactory().constructCollectionType(Collection.class, PersonneDTO.class);
                    return objectMapper.readValue(body, collectionType);
                }
                case STATUS_UNAUTHORIZED: {
                    throw new TokenExpireException();
                }
                case STATUS_FORBIDDEN: {
                    throw new DroitsInsuffisantsException();
                }
                default:
                    throw new ErreurInattendueException(String.format(ERREUR_STATUS_INATTENDU, response.statusCode()));
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }



    public PersonneDTO getPersonneById(String token, String id) throws DroitsInsuffisantsException, TokenExpireException, UtilisateurInexistantException {
        HttpRequest request = null;

        try {
            request = HttpRequest.newBuilder()
                    .uri(URI.create(URI_USERS + "/" + id))
                    .setHeader(ENTETE_ACCEPT, CONTENT_TYPE_APPLICATION_JSON)
                    .setHeader(ENTETE_TOKEN, token)
                    .build();
        }
        catch (Exception e) {
            throw new ErreurInattendueException("Erreur lors de l'envoi de la requête : " + e.getMessage());
        }

        HttpResponse<String> response = null;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            switch (response.statusCode()) {
                case STATUS_OK: {
                    String body = response.body();
                    return objectMapper.readValue(body, PersonneDTO.class);
                }
                case STATUS_UNAUTHORIZED: {
                    throw new TokenExpireException();
                }
                case STATUS_FORBIDDEN: {
                    throw new DroitsInsuffisantsException();
                }
                case STATUS_NOT_FOUND: {
                    throw new UtilisateurInexistantException();
                }
                default:
                    throw new ErreurInattendueException(String.format(ERREUR_STATUS_INATTENDU, response.statusCode()));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }


}
