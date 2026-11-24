package fr.miage.mindcrime.moteur.contraintes;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record CleVariable(UUID partieId, UUID suspectConcerneId, Instant instant) {
    public CleVariable {
        Objects.requireNonNull(partieId, "partieId");
        Objects.requireNonNull(suspectConcerneId, "suspectConcerneId");
        Objects.requireNonNull(instant, "instant");
        if (instant.getNano() != 0) {
            throw new ErreurContratAnalyse("La clé temporelle doit être précise à la seconde.");
        }
    }
}
