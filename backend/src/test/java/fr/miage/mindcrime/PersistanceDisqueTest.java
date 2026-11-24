package fr.miage.mindcrime;

import fr.miage.mindcrime.infrastructure.persistence.DepotTemoignagesJdbc;
import fr.miage.mindcrime.moteur.contraintes.Declaration;
import fr.miage.mindcrime.moteur.contraintes.NatureDeclaration;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.support.TransactionTemplate;
import java.nio.file.Path;
import java.time.Instant;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class PersistanceDisqueTest {
    @TempDir Path dossier;

    @Test void temoignageEtRevisionSurviventAFermetureEtReouvertureDeLaBase() {
        var url = "jdbc:h2:file:" + dossier.resolve("durable").toAbsolutePath().toString().replace('\\', '/')
                + ";MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE";
        var source = new DriverManagerDataSource(url, "sa", "");
        var flyway = Flyway.configure().dataSource(source).locations("classpath:db/migration/contraintes").load();
        flyway.migrate();
        var jdbc = new JdbcTemplate(source);
        var partie = new UUID(0, 1); var auteur = new UUID(0, 3); var lieu = new UUID(0, 5);
        jdbc.update("INSERT INTO d2_dossier(id,titre,fuseau) VALUES (?,'Test durable','UTC')", partie);
        jdbc.update("INSERT INTO d2_acteur(partie_id,id,nom) VALUES (?,?,'Témoin')", partie, auteur);
        jdbc.update("INSERT INTO d2_lieu(partie_id,id,nom) VALUES (?,?,'Cuisine')", partie, lieu);
        jdbc.update("INSERT INTO d2_acces(partie_id,lecteur_id) VALUES (?,?)", partie, auteur);
        var depot = new DepotTemoignagesJdbc(jdbc, new TransactionTemplate(new DataSourceTransactionManager(source)));
        var declaration = new Declaration(UUID.randomUUID(), partie, auteur, auteur, lieu,
                Instant.parse("2026-11-24T20:00:00Z"), NatureDeclaration.PRESENCE);
        depot.dansTransaction(partie, auteur, ecriture -> { ecriture.enregistrer(declaration); return true; });
        var avant = depot.chargerAccessibles(partie, auteur);
        jdbc.execute("SHUTDOWN");

        var nouvelleSource = new DriverManagerDataSource(url, "sa", "");
        var migrations = Flyway.configure().dataSource(nouvelleSource).locations("classpath:db/migration/contraintes").load().migrate();
        assertEquals(0, migrations.migrationsExecuted);
        var nouveauDepot = new DepotTemoignagesJdbc(new JdbcTemplate(nouvelleSource),
                new TransactionTemplate(new DataSourceTransactionManager(nouvelleSource)));
        var apres = nouveauDepot.chargerAccessibles(partie, auteur);
        assertEquals(avant, apres);
        assertEquals(java.util.List.of(declaration), apres.declarations());
        new JdbcTemplate(nouvelleSource).execute("SHUTDOWN");
    }
}
