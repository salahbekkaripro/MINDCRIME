package fr.miage.mindcrime.moteur.modele;

import java.util.Objects;

public record Indice(
        String id,
        TypeIndice type,
        String suspectId,
        String lieuId,
        Creneau creneau,
        String description,
        boolean visible
) {
    public Indice {
        Objects.requireNonNull(id, "id ne peut pas etre null");
        Objects.requireNonNull(type, "type ne peut pas etre null");
        Objects.requireNonNull(suspectId, "suspectId ne peut pas etre null");
        Objects.requireNonNull(lieuId, "lieuId ne peut pas etre null");
        Objects.requireNonNull(creneau, "creneau ne peut pas etre null");
        Objects.requireNonNull(description, "description ne peut pas etre null");
    }

    public Indice avecVisibilite(boolean nouvelleVisibilite) {
        return new Indice(id, type, suspectId, lieuId, creneau, description, nouvelleVisibilite);
    }
}