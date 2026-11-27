package fr.miage.mindcrime.moteur.generation;

import fr.miage.mindcrime.moteur.modele.Enquete;
import fr.miage.mindcrime.moteur.modele.StatutUnicite;

import java.util.Objects;
import java.util.Optional;

public record ResultatGeneration(
        StatutUnicite statut,
        Enquete enquete,
        int noeudsExplores
) {
    public ResultatGeneration {
        Objects.requireNonNull(statut, "statut ne peut pas etre null");
        if (noeudsExplores < 0) {
            throw new IllegalArgumentException("noeudsExplores doit etre positif ou nul");
        }
    }

    public Optional<Enquete> enqueteOpt() {
        return Optional.ofNullable(enquete);
    }
}