package fr.miage.mindcrime.moteur.contraintes;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/** Ensemble complet pour le périmètre fourni ; l'appelant garantit visibilité et références. */
public record InstantaneDeclarations(UUID partieId, String revision, String perimetre,
                                     List<Declaration> declarations,
                                     List<DeclarationNonExploitable> nonExploitables) {
    public InstantaneDeclarations {
        Objects.requireNonNull(partieId, "partieId");
        if (Objects.requireNonNull(revision, "revision").isBlank()
                || Objects.requireNonNull(perimetre, "perimetre").isBlank()) {
            throw new ErreurContratAnalyse("Révision et périmètre sont obligatoires.");
        }
        declarations = List.copyOf(declarations);
        nonExploitables = List.copyOf(nonExploitables);
    }
}
