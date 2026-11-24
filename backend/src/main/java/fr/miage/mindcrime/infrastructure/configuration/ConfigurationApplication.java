package fr.miage.mindcrime.infrastructure.configuration;

import fr.miage.mindcrime.application.alibi.*;
import fr.miage.mindcrime.application.port.PortDossierTemoignages;
import fr.miage.mindcrime.application.port.IdentiteLecteur;
import fr.miage.mindcrime.infrastructure.persistence.DepotTemoignagesJdbc;
import fr.miage.mindcrime.moteur.contraintes.AnalyseurContraintesUnaires;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import java.util.UUID;

@Configuration
public class ConfigurationApplication {
    @Bean AnalyseurContraintesUnaires analyseur() { return new AnalyseurContraintesUnaires(); }
    @Bean PortDossierTemoignages depot(JdbcTemplate jdbc, PlatformTransactionManager transactions) {
        return new DepotTemoignagesJdbc(jdbc, new TransactionTemplate(transactions));
    }
    @Bean AnalyserTemoignages analyserTemoignages(PortDossierTemoignages depot, AnalyseurContraintesUnaires analyseur) {
        return new AnalyserTemoignages(depot, analyseur);
    }
    @Bean ConsulterTemoignages consulterTemoignages(PortDossierTemoignages depot) { return new ConsulterTemoignages(depot); }
    @Bean SoumettreTemoignage soumettreTemoignage(PortDossierTemoignages depot, AnalyseurContraintesUnaires analyseur) {
        return new SoumettreTemoignage(depot, analyseur);
    }
    @Bean IdentiteLecteur identite(@Value("${mindcrime.compte.acteur-id}") UUID acteur,
                                 @Value("${mindcrime.compte.nom}") String nom,
                                 @Value("${mindcrime.dossier-id:00000000-0000-0000-0000-000000000001}") UUID dossier) {
        return new IdentiteLecteur(acteur, nom, dossier);
    }
}
