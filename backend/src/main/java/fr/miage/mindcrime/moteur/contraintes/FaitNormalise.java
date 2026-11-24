package fr.miage.mindcrime.moteur.contraintes;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public record FaitNormalise(CleFait cle, Set<UUID> sources) {
    public FaitNormalise {
        Objects.requireNonNull(cle, "cle");
        sources = Set.copyOf(sources);
        if (sources.isEmpty()) {
            throw new ErreurContratAnalyse("Un fait doit conserver au moins une source.");
        }
    }
}
