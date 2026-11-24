package fr.miage.mindcrime.moteur.contraintes;

import java.util.Objects;
import java.util.UUID;

public record CleFait(CleVariable variable, UUID lieuId, NatureDeclaration nature) {
    public CleFait {
        Objects.requireNonNull(variable, "variable");
        Objects.requireNonNull(lieuId, "lieuId");
        Objects.requireNonNull(nature, "nature");
    }
}
