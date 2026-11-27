package fr.miage.mindcrime.moteur.modele;

import java.util.Objects;

public record Crime(String lieuId, Creneau creneau) {
    public Crime {
        Objects.requireNonNull(lieuId, "lieuId ne peut pas etre null");
        Objects.requireNonNull(creneau, "creneau ne peut pas etre null");
    }
}