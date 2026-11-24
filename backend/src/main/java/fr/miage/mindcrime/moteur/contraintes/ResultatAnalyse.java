package fr.miage.mindcrime.moteur.contraintes;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public record ResultatAnalyse(UUID partieId, String revision, String perimetre,
                              VerdictAnalyse verdict, CompletudeAnalyse completude,
                              Map<CleFait, FaitNormalise> faits, Set<Conflit> conflits,
                              List<DeclarationNonExploitable> nonExploitables) {
    public ResultatAnalyse {
        Objects.requireNonNull(partieId, "partieId");
        Objects.requireNonNull(revision, "revision");
        Objects.requireNonNull(perimetre, "perimetre");
        Objects.requireNonNull(verdict, "verdict");
        Objects.requireNonNull(completude, "completude");
        faits = Map.copyOf(faits);
        conflits = Set.copyOf(conflits);
        nonExploitables = List.copyOf(nonExploitables);
    }
}
