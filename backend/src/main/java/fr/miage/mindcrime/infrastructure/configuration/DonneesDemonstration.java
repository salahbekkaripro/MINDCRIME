package fr.miage.mindcrime.infrastructure.configuration;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import java.util.UUID;

/** Jeu fixe et idempotent, substitut provisoire des référentiels D1/D4. */
@Configuration
@Profile("demo")
public class DonneesDemonstration {
    @Bean ApplicationRunner initialiserDemonstration(JdbcTemplate jdbc, PlatformTransactionManager transactions) {
        return args -> new TransactionTemplate(transactions).executeWithoutResult(statut -> {
            var partie = new UUID(0, 1);
            if (jdbc.queryForObject("SELECT COUNT(*) FROM d2_dossier WHERE id=?", Integer.class, partie) != 0) return;
            jdbc.update("INSERT INTO d2_dossier(id,titre,fuseau) VALUES (?,?,?)", partie, "Le manoir des brumes", "UTC");
            for (var acteur : new String[][]{{"3", "Camille — témoin"}, {"4", "Alicia Morel"}, {"8", "Bastien Roux"}, {"9", "Clara Vidal"}}) {
                jdbc.update("INSERT INTO d2_acteur(partie_id,id,nom) VALUES (?,?,?)", partie,
                        new UUID(0, Long.parseLong(acteur[0])), acteur[1]);
            }
            for (var lieu : new String[][]{{"5", "Cuisine"}, {"6", "Jardin"}, {"7", "Salon"}}) {
                jdbc.update("INSERT INTO d2_lieu(partie_id,id,nom) VALUES (?,?,?)", partie,
                        new UUID(0, Long.parseLong(lieu[0])), lieu[1]);
            }
            jdbc.update("INSERT INTO d2_acces(partie_id,lecteur_id) VALUES (?,?)", partie, new UUID(0, 3));
        });
    }
}
