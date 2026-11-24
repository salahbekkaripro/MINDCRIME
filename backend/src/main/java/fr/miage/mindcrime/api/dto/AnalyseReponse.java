package fr.miage.mindcrime.api.dto;

import fr.miage.mindcrime.moteur.contraintes.*;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/** DTO de sortie explicite : aucune sérialisation d'une Map à clés métier complexes. */
public record AnalyseReponse(UUID partieId, String revision, VerdictAnalyse verdict,
                             CompletudeAnalyse completude, int declarationsAnalysees,
                             List<ConflitReponse> conflits, List<DeclarationNonExploitable> nonExploitables) {
    public record FaitReponse(UUID suspectConcerneId, UUID lieuId, Instant instant,
                             NatureDeclaration nature, List<UUID> sources) { }
    public record ConflitReponse(MotifConflit motif, String message, List<FaitReponse> faits) { }

    public static AnalyseReponse depuis(ResultatAnalyse resultat) {
        var conflits = resultat.conflits().stream().map(conflit -> new ConflitReponse(conflit.motif(),
                conflit.motif() == MotifConflit.LIEUX_DISTINCTS
                        ? "Le même suspect est déclaré présent dans deux lieux au même instant."
                        : "Le même suspect est déclaré présent et absent du même lieu au même instant.",
                conflit.faits().stream().sorted(Comparator.comparing(cle -> cle.lieuId() + cle.nature().name()))
                        .map(cle -> new FaitReponse(cle.variable().suspectConcerneId(), cle.lieuId(),
                                cle.variable().instant(), cle.nature(), resultat.faits().get(cle).sources().stream()
                                .sorted().toList())).toList()))
                .sorted(Comparator.comparing(conflit -> conflit.motif().name() + conflit.faits().toString())).toList();
        int nombre = resultat.faits().values().stream().mapToInt(fait -> fait.sources().size()).sum();
        return new AnalyseReponse(resultat.partieId(), resultat.revision(), resultat.verdict(), resultat.completude(),
                nombre, conflits, resultat.nonExploitables());
    }
}
