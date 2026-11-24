package fr.miage.mindcrime.application.alibi;

import fr.miage.mindcrime.moteur.contraintes.NatureDeclaration;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record CommandeTemoignage(UUID suspectConcerneId, UUID lieuId, Instant instant, NatureDeclaration nature) {
    public CommandeTemoignage {
        Objects.requireNonNull(suspectConcerneId);
        Objects.requireNonNull(lieuId);
        Objects.requireNonNull(instant);
        Objects.requireNonNull(nature);
    }
}
