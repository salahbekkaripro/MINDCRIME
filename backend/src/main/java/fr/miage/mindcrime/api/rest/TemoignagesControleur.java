package fr.miage.mindcrime.api.rest;

import fr.miage.mindcrime.api.dto.*;
import fr.miage.mindcrime.application.alibi.*;
import fr.miage.mindcrime.application.port.*;
import fr.miage.mindcrime.moteur.contraintes.Declaration;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class TemoignagesControleur {
    private final ConsulterTemoignages consulter;
    private final AnalyserTemoignages analyser;
    private final SoumettreTemoignage soumettre;
    private final IdentiteLecteur identite;

    public TemoignagesControleur(ConsulterTemoignages consulter, AnalyserTemoignages analyser,
                                 SoumettreTemoignage soumettre, IdentiteLecteur identite) {
        this.consulter = consulter; this.analyser = analyser; this.soumettre = soumettre; this.identite = identite;
    }
    public record SessionReponse(UUID lecteurId, String nom, UUID partieId, String csrfToken) { }
    public record SoumissionReponse(Declaration declaration, AnalyseReponse analyse) { }
    public record ListeReponse(String revision, List<Declaration> declarations) { }

    @GetMapping("/session") SessionReponse session(CsrfToken csrf) {
        return new SessionReponse(identite.acteurId(), identite.nom(), identite.dossierId(), csrf.getToken());
    }
    @GetMapping("/parties/{partieId}/referentiel") ReferentielDossier referentiel(@PathVariable UUID partieId) {
        return consulter.referentiel(partieId, identite.acteurId());
    }
    @GetMapping("/parties/{partieId}/temoignages") ListeReponse temoignages(@PathVariable UUID partieId) {
        var instantane = consulter.liste(partieId, identite.acteurId());
        return new ListeReponse(instantane.revision(), instantane.declarations());
    }
    @GetMapping("/parties/{partieId}/temoignages/{declarationId}")
    Declaration declaration(@PathVariable UUID partieId, @PathVariable UUID declarationId) {
        return consulter.declaration(partieId, identite.acteurId(), declarationId);
    }
    @GetMapping("/parties/{partieId}/contradictions") AnalyseReponse contradictions(@PathVariable UUID partieId) {
        return AnalyseReponse.depuis(analyser.executer(partieId, identite.acteurId()));
    }
    @PostMapping(value="/parties/{partieId}/temoignages", consumes=MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<SoumissionReponse> soumettre(@PathVariable UUID partieId, @Valid @RequestBody NouveauTemoignage corps) {
        var resultat = soumettre.executer(partieId, identite.acteurId(), corps.commande());
        return ResponseEntity.created(URI.create("/api/v1/parties/" + partieId + "/temoignages/" + resultat.declaration().id()))
                .body(new SoumissionReponse(resultat.declaration(), AnalyseReponse.depuis(resultat.analyse())));
    }
}
