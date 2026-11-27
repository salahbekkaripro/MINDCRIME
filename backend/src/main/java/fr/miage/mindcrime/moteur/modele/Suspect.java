package fr.miage.mindcrime.moteur.modele;

import java.util.Objects;

public record Suspect(String id, String nom, String profil) {
    public Suspect {
        Objects.requireNonNull(id, "id ne peut pas etre null");
        Objects.requireNonNull(nom, "nom ne peut pas etre null");
        Objects.requireNonNull(profil, "profil ne peut pas etre null");
    }
}