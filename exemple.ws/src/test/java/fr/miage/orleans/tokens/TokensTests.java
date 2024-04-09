package fr.miage.orleans.tokens;

import fr.miage.orleans.tokens.facades.Facade;
import fr.miage.orleans.tokens.facades.Personne;
import fr.miage.orleans.tokens.facades.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TokensTests {


    @Autowired
    MockMvc mvc;



    @MockBean
    Facade facade;



    @Test
    public void testGetAllsOK() throws Exception {


        doReturn(Arrays.asList(
                new Personne(1,"yohan.boichut@univ-orleans.fr",
                        "yohan","boichut","password",new Role[]{Role.USER}
                ))).when(facade).getAll();


        mvc.perform(get("/api/users").with(jwt()
                        .authorities(List.of(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .jwt(jwt ->jwt.claim(StandardClaimNames.SUB, "yohan.boichut@univ-orleans.fr").claim("id","1"))))

                .andExpect(status().isOk()).
        andExpect(e -> {
            System.out.println(e.getResponse().getContentAsString());
        });


    }


    @Test
    public void testGetAllsKO() throws Exception {

        mvc.perform(get("/api/users").with(jwt()
                        .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                        .jwt(jwt ->jwt.claim(StandardClaimNames.SUB, "yohan.boichut@univ-orleans.fr").claim("id","1"))))

                .andExpect(status().isForbidden()).
                andExpect(e -> {
                    System.out.println(e.getResponse().getContentAsString());
                });


    }


    @Test
    public void testGetOne() throws Exception {


        doReturn(Optional.of(new Personne(0,"yohan.boichut@univ-orleans.fr","yohan","boichut","password",new Role[]{Role.USER}))).when(facade).getPersonneById(0);

        mvc.perform(get("/api/users/0").with(jwt()
                        .authorities(List.of(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .jwt(jwt ->jwt.claim(StandardClaimNames.SUB, "yohan.boichut@univ-orleans.fr").claim("id","1"))))

                .andExpect(status().isOk()).
                andExpect(e -> {
                    System.out.println(e.getResponse().getContentAsString());
                });


    }






}
