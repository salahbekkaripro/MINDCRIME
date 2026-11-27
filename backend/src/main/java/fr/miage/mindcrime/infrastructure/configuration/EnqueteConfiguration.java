package fr.miage.mindcrime.infrastructure.configuration;

import fr.miage.mindcrime.application.enqueteur.ConsulterEnqueteUseCase;
import fr.miage.mindcrime.application.enqueteur.GenererEnqueteUseCase;
import fr.miage.mindcrime.application.port.EnqueteRepositoryPort;
import fr.miage.mindcrime.moteur.generation.GenerateurEnqueteBacktracking;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class EnqueteConfiguration {

    @Bean
    public GenerateurEnqueteBacktracking generateurEnqueteBacktracking() {
        return new GenerateurEnqueteBacktracking();
    }

    @Bean
    public GenererEnqueteUseCase genererEnqueteUseCase(
            GenerateurEnqueteBacktracking generateur,
            EnqueteRepositoryPort repository
    ) {
        return new GenererEnqueteUseCase(generateur, repository);
    }

    @Bean
    public ConsulterEnqueteUseCase consulterEnqueteUseCase(EnqueteRepositoryPort repository) {
        return new ConsulterEnqueteUseCase(repository);
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins("http://localhost:5173", "http://127.0.0.1:5173")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
            }
        };
    }
}