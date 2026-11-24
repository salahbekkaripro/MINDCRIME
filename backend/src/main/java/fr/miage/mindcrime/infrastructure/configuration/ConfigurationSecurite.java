package fr.miage.mindcrime.infrastructure.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

@Configuration
public class ConfigurationSecurite {
    @Bean UserDetailsService comptes(@Value("${mindcrime.compte.nom}") String nom,
                                    @Value("${mindcrime.compte.mot-de-passe}") String motDePasse) {
        if (nom.isBlank() || motDePasse.isBlank()) throw new IllegalArgumentException("Compte local obligatoire.");
        return new InMemoryUserDetailsManager(User.withUsername(nom)
                .password("{bcrypt}" + new BCryptPasswordEncoder().encode(motDePasse)).roles("JOUEUR").build());
    }
    @Bean SecurityFilterChain securite(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(acces -> acces.requestMatchers("/api/**").authenticated().anyRequest().permitAll())
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(csrf -> csrf.csrfTokenRepository(new CookieCsrfTokenRepository())
                        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
                        // Basic stateless réauthentifie chaque requête : garder le jeton du cookie
                        // au lieu de le remplacer à chaque requête. La validation CSRF reste active.
                        .sessionAuthenticationStrategy(new NullAuthenticatedSessionStrategy()))
                .exceptionHandling(erreurs -> erreurs
                        .authenticationEntryPoint((requete, reponse, exception) -> {
                            reponse.setStatus(401);
                            reponse.setCharacterEncoding("UTF-8");
                            reponse.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
                            reponse.getWriter().write("{\"type\":\"about:blank\",\"title\":\"Authentification requise\",\"status\":401}");
                        })
                        .accessDeniedHandler((requete, reponse, exception) -> {
                            reponse.setStatus(403);
                            reponse.setCharacterEncoding("UTF-8");
                            reponse.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
                            reponse.getWriter().write("{\"type\":\"about:blank\",\"title\":\"Requête non autorisée\",\"status\":403}");
                        }));
        return http.build();
    }
}
