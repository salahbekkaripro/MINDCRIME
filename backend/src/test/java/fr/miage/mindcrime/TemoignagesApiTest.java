package fr.miage.mindcrime;

import fr.miage.mindcrime.application.port.PortDossierTemoignages;
import fr.miage.mindcrime.moteur.contraintes.Declaration;
import fr.miage.mindcrime.moteur.contraintes.NatureDeclaration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import java.net.CookieManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.Executors;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "spring.datasource.url=jdbc:h2:mem:api;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1",
        "logging.level.org.springframework=INFO", "debug=false"
})
@ActiveProfiles("demo")
class TemoignagesApiTest {
    @LocalServerPort int port;
    @Autowired JdbcTemplate jdbc;
    @Autowired PortDossierTemoignages depot;
    private final JsonMapper json = JsonMapper.builder().build();
    private HttpClient client;
    private String csrf;
    private static final UUID PARTIE = new UUID(0, 1), AUTEUR = new UUID(0, 3);
    private static final String BASE = "/api/v1/parties/" + PARTIE;
    private static final String AUTH = "Basic " + Base64.getEncoder().encodeToString("demo:mindcrime-demo".getBytes(java.nio.charset.StandardCharsets.UTF_8));

    @BeforeEach void preparer() throws Exception {
        jdbc.update("DELETE FROM d2_temoignage");
        client = HttpClient.newBuilder().cookieHandler(new CookieManager()).build();
        var session = get("/api/v1/session");
        assertEquals(200, session.statusCode());
        csrf = json.readTree(session.body()).get("csrfToken").asText();
    }
    private HttpResponse<String> get(String chemin) throws Exception {
        return client.send(HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + chemin))
                .header("Authorization", AUTH).GET().build(), HttpResponse.BodyHandlers.ofString());
    }
    private HttpResponse<String> post(String chemin, String corps) throws Exception {
        return client.send(HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + chemin))
                .header("Authorization", AUTH).header("Content-Type", "application/json")
                .header("X-XSRF-TOKEN", csrf).POST(HttpRequest.BodyPublishers.ofString(corps)).build(),
                HttpResponse.BodyHandlers.ofString());
    }
    private String corps(long lieu) {
        return "{\"suspectConcerneId\":\"" + new UUID(0, 4) + "\",\"lieuId\":\"" + new UUID(0, lieu)
                + "\",\"instant\":\"2026-11-24T20:00:00Z\",\"nature\":\"PRESENCE\"}";
    }
    private int nombre() { return jdbc.queryForObject("SELECT COUNT(*) FROM d2_temoignage", Integer.class); }
    private JsonNode arbre(HttpResponse<String> reponse) { return json.readTree(reponse.body()); }

    @Test void deuxSoumissionsSontConserveesEtLeConflitEstUnSucces201() throws Exception {
        var premiere = post(BASE + "/temoignages", corps(5));
        assertEquals(201, premiere.statusCode(), premiere.body());
        assertEquals("AUCUNE_CONTRADICTION_DETECTEE", arbre(premiere).at("/analyse/verdict").asText());
        var seconde = post(BASE + "/temoignages", corps(6));
        assertEquals(201, seconde.statusCode(), seconde.body());
        assertEquals("CONTRADICTION", arbre(seconde).at("/analyse/verdict").asText());
        assertEquals("LIEUX_DISTINCTS", arbre(seconde).at("/analyse/conflits/0/motif").asText());
        assertEquals(AUTEUR.toString(), arbre(seconde).at("/declaration/auteurId").asText());
        assertTrue(seconde.headers().firstValue("Location").orElseThrow().startsWith(BASE + "/temoignages"));
        var creee = get(seconde.headers().firstValue("Location").orElseThrow());
        assertEquals(200, creee.statusCode());
        assertEquals(arbre(seconde).get("declaration"), arbre(creee));
        var liste = arbre(get(BASE + "/temoignages"));
        assertEquals(2, liste.get("declarations").size());
        assertEquals(2, nombre());
        assertEquals(liste.get("revision").asText(), arbre(get(BASE + "/contradictions")).get("revision").asText());
    }

    @Test void repetitionConserveTroisSourcesMaisUnSeulConflit() throws Exception {
        post(BASE + "/temoignages", corps(5));
        post(BASE + "/temoignages", corps(6));
        var repetition = post(BASE + "/temoignages", corps(5));
        assertEquals(201, repetition.statusCode());
        assertEquals(1, arbre(repetition).at("/analyse/conflits").size());
        assertEquals(3, arbre(repetition).at("/analyse/declarationsAnalysees").asInt());
    }

    @Test void champsManquantsNeSontPasEnregistresCommeContradiction() throws Exception {
        var reponse = post(BASE + "/temoignages", "{}");
        assertEquals(422, reponse.statusCode(), reponse.body());
        assertTrue(reponse.headers().firstValue("Content-Type").orElseThrow().contains("application/problem+json"));
        assertEquals(0, nombre());
    }

    @Test void jsonInvalideEtValeursInconnuesSontDesErreursDeRequete() throws Exception {
        assertEquals(400, post(BASE + "/temoignages", "{").statusCode());
        assertEquals(400, post(BASE + "/temoignages", corps(5).replace("PRESENCE", "INCONNUE")).statusCode());
        assertEquals(400, post(BASE + "/temoignages", corps(5).replace("2026-11-24T20:00:00Z", "20h")).statusCode());
        assertEquals(422, post(BASE + "/temoignages", corps(5).replace("20:00:00Z", "20:00:00.000000001Z")).statusCode());
        assertEquals(0, nombre());
    }

    @Test void impossibleDImposerAuteurOuVisibiliteDansLeCorps() throws Exception {
        var avecAuteur = corps(5).replace("}", ",\"auteurId\":\"" + new UUID(0, 8) + "\"}");
        assertEquals(400, post(BASE + "/temoignages", avecAuteur).statusCode());
        assertEquals(400, post(BASE + "/temoignages", corps(5).replace("}", ",\"visibilite\":\"PRIVE\"}")).statusCode());
        assertEquals(0, nombre());
    }

    @Test void partieOuReferenceInaccessibleNeRevelePasSonExistence() throws Exception {
        assertEquals(404, get("/api/v1/parties/" + new UUID(0, 99) + "/temoignages").statusCode());
        assertEquals(404, post(BASE + "/temoignages", corps(99)).statusCode());
        assertEquals(0, nombre());
    }

    @Test void sansAuthentificationLAnalyseEstRefusee() throws Exception {
        var reponse = client.send(HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + BASE + "/contradictions"))
                .GET().build(), HttpResponse.BodyHandlers.ofString());
        assertEquals(401, reponse.statusCode());
    }

    @Test void sansJetonCsrfLEcritureEstRefusee() throws Exception {
        var reponse = client.send(HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + BASE + "/temoignages"))
                .header("Authorization", AUTH).header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(corps(5))).build(), HttpResponse.BodyHandlers.ofString());
        assertEquals(403, reponse.statusCode());
        assertEquals(0, nombre());
    }

    @Test void temoignagePriveNeChangeNiVerdictNiRevisionNiListePublique() throws Exception {
        assertEquals(201, post(BASE + "/temoignages", corps(5)).statusCode());
        var analyseAvant = arbre(get(BASE + "/contradictions"));
        var listeAvant = arbre(get(BASE + "/temoignages"));
        var identifiantCache = UUID.randomUUID();
        inserer(identifiantCache, new UUID(0, 8), 6, "PRIVE");
        assertEquals(analyseAvant, arbre(get(BASE + "/contradictions")));
        assertEquals(listeAvant, arbre(get(BASE + "/temoignages")));
        assertEquals(404, get(BASE + "/temoignages/" + identifiantCache).statusCode());
    }

    @Test void depasserLaLimiteAnnuleLEcritureSansTronquerLesDonnees() throws Exception {
        for (int i = 0; i < 200; i++) inserer(UUID.randomUUID(), AUTEUR, 5, "PUBLIC");
        var reponse = post(BASE + "/temoignages", corps(6));
        assertEquals(422, reponse.statusCode(), reponse.body());
        assertEquals(200, nombre());
    }

    @Test void unePannePendantLaTransactionAnnuleLEcriture() {
        assertThrows(IllegalStateException.class, () -> depot.dansTransaction(PARTIE, AUTEUR, ecriture -> {
            ecriture.enregistrer(new Declaration(UUID.randomUUID(), PARTIE, AUTEUR, new UUID(0, 4),
                    new UUID(0, 5), Instant.parse("2026-11-24T20:00:00Z"), NatureDeclaration.PRESENCE));
            throw new IllegalStateException("Panne simulée après insertion");
        }));
        assertEquals(0, nombre());
    }

    @Test void soumissionsConcurrentesSontSerialiseesParPartie() throws Exception {
        try (var executant = Executors.newFixedThreadPool(2)) {
            var a = executant.submit(() -> post(BASE + "/temoignages", corps(5)));
            var b = executant.submit(() -> post(BASE + "/temoignages", corps(6)));
            var ra = a.get(); var rb = b.get();
            assertEquals(201, ra.statusCode(), ra.body());
            assertEquals(201, rb.statusCode(), rb.body());
            var verdicts = java.util.Set.of(arbre(ra).at("/analyse/verdict").asText(), arbre(rb).at("/analyse/verdict").asText());
            assertEquals(java.util.Set.of("CONTRADICTION", "AUCUNE_CONTRADICTION_DETECTEE"), verdicts);
            assertEquals(2, nombre());
        }
    }

    private void inserer(UUID id, UUID auteur, long lieu, String visibilite) {
        jdbc.update("""
                INSERT INTO d2_temoignage(id,partie_id,auteur_id,suspect_id,lieu_id,instant,nature,visibilite)
                VALUES (?,?,?,?,?,TIMESTAMP WITH TIME ZONE '2026-11-24 20:00:00+00','PRESENCE',?)
                """, id, PARTIE, auteur, new UUID(0, 4), new UUID(0, lieu), visibilite);
    }
}
