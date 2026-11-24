package fr.miage.mindcrime.infrastructure.persistence;

import fr.miage.mindcrime.application.alibi.ContexteInaccessible;
import fr.miage.mindcrime.application.port.*;
import fr.miage.mindcrime.moteur.contraintes.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

/** Adaptateur SQL D2 ; référentiels provisoires explicitement limités à cette tranche. */
public final class DepotTemoignagesJdbc implements PortDossierTemoignages {
    private final JdbcTemplate jdbc;
    private final TransactionTemplate transaction;

    public DepotTemoignagesJdbc(JdbcTemplate jdbc, TransactionTemplate transaction) {
        this.jdbc = jdbc;
        this.transaction = transaction;
    }

    private void verifierAcces(UUID partieId, UUID lecteurId) {
        var nombre = jdbc.queryForObject("SELECT COUNT(*) FROM d2_acces WHERE partie_id=? AND lecteur_id=?",
                Integer.class, partieId, lecteurId);
        if (nombre == null || nombre != 1) throw new ContexteInaccessible();
    }

    @Override
    public InstantaneDeclarations chargerAccessibles(UUID partieId, UUID lecteurId) {
        verifierAcces(partieId, lecteurId);
        return lireInstantane(partieId, lecteurId);
    }

    private InstantaneDeclarations lireInstantane(UUID partieId, UUID lecteurId) {
        var declarations = jdbc.query("""
                SELECT id,partie_id,auteur_id,suspect_id,lieu_id,instant,nature
                FROM d2_temoignage WHERE partie_id=? AND (visibilite='PUBLIC' OR auteur_id=?)
                ORDER BY instant,id
                """, (rs, ligne) -> new Declaration(rs.getObject("id", UUID.class),
                rs.getObject("partie_id", UUID.class), rs.getObject("auteur_id", UUID.class),
                rs.getObject("suspect_id", UUID.class), rs.getObject("lieu_id", UUID.class),
                rs.getObject("instant", OffsetDateTime.class).toInstant(),
                NatureDeclaration.valueOf(rs.getString("nature"))), partieId, lecteurId);
        return new InstantaneDeclarations(partieId, revisionVisible(declarations),
                PortDeclarations.perimetreLecteur(lecteurId), declarations, List.of());
    }

    private String revisionVisible(List<Declaration> declarations) {
        try {
            var digest = MessageDigest.getInstance("SHA-256");
            declarations.stream().sorted(Comparator.comparing(d -> d.id().toString()))
                    .forEach(d -> digest.update((d.toString() + "\n").getBytes(StandardCharsets.UTF_8)));
            return HexFormat.of().formatHex(digest.digest());
        } catch (NoSuchAlgorithmException impossible) {
            throw new IllegalStateException("SHA-256 indisponible", impossible);
        }
    }

    @Override
    public ReferentielDossier referentiel(UUID partieId, UUID lecteurId) {
        verifierAcces(partieId, lecteurId);
        var dossier = jdbc.queryForMap("SELECT titre,fuseau FROM d2_dossier WHERE id=?", partieId);
        var suspects = jdbc.query("SELECT id,nom FROM d2_acteur WHERE partie_id=? ORDER BY nom",
                (rs, i) -> new ReferenceDossier(rs.getObject("id", UUID.class), rs.getString("nom")), partieId);
        var lieux = jdbc.query("SELECT id,nom FROM d2_lieu WHERE partie_id=? ORDER BY nom",
                (rs, i) -> new ReferenceDossier(rs.getObject("id", UUID.class), rs.getString("nom")), partieId);
        return new ReferentielDossier(partieId, (String) dossier.get("titre"), (String) dossier.get("fuseau"), suspects, lieux);
    }

    @Override
    public <T> T dansTransaction(UUID partieId, UUID lecteurId, Function<Ecriture, T> operation) {
        return Objects.requireNonNull(transaction.execute(statut -> {
            verifierAcces(partieId, lecteurId);
            jdbc.queryForObject("SELECT id FROM d2_dossier WHERE id=? FOR UPDATE", UUID.class, partieId);
            return operation.apply(new Ecriture() {
                @Override public void verifierReferences(UUID suspectId, UUID lieuId) {
                    var suspects = jdbc.queryForObject("SELECT COUNT(*) FROM d2_acteur WHERE partie_id=? AND id=?",
                            Integer.class, partieId, suspectId);
                    var lieux = jdbc.queryForObject("SELECT COUNT(*) FROM d2_lieu WHERE partie_id=? AND id=?",
                            Integer.class, partieId, lieuId);
                    if (!Integer.valueOf(1).equals(suspects) || !Integer.valueOf(1).equals(lieux)) {
                        throw new ContexteInaccessible();
                    }
                }
                @Override public void enregistrer(Declaration d) {
                    if (!d.partieId().equals(partieId) || !d.auteurId().equals(lecteurId)) {
                        throw new ErreurContratAnalyse("Contexte d'écriture incohérent.");
                    }
                    jdbc.update("""
                            INSERT INTO d2_temoignage(id,partie_id,auteur_id,suspect_id,lieu_id,instant,nature,visibilite)
                            VALUES (?,?,?,?,?,?,?,'PUBLIC')
                            """, d.id(), d.partieId(), d.auteurId(), d.suspectConcerneId(), d.lieuId(),
                            OffsetDateTime.ofInstant(d.instant(), ZoneOffset.UTC), d.nature().name());
                }
                @Override public InstantaneDeclarations charger() { return lireInstantane(partieId, lecteurId); }
            });
        }));
    }
}
