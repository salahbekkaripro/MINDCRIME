package fr.miage.mindcrime.moteur.contraintes;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/** Contrat D2 provisoire : aucune entité Partie/Suspect d'un autre domaine n'est recréée. */
public record Declaration(UUID id, UUID partieId, UUID auteurId, UUID suspectConcerneId,
                          UUID lieuId, Instant instant, NatureDeclaration nature) {
    public Declaration {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(partieId, "partieId");
        Objects.requireNonNull(auteurId, "auteurId");
        Objects.requireNonNull(suspectConcerneId, "suspectConcerneId");
        Objects.requireNonNull(lieuId, "lieuId");
        Objects.requireNonNull(instant, "instant");
        Objects.requireNonNull(nature, "nature");
        if (instant.getNano() != 0) {
            throw new ErreurContratAnalyse("L'instant doit être précis à la seconde, sans arrondi.");
        }
    }

    public CleFait cleFait() {
        return new CleFait(new CleVariable(partieId, suspectConcerneId, instant), lieuId, nature);
    }
}
