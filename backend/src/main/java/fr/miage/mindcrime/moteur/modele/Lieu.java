package fr.miage.mindcrime.moteur.modele;

import java.util.Objects;

public record Lieu(String id, String nom) {
    public Lieu {
        Objects.requireNonNull(id, "id ne peut pas etre null");
        Objects.requireNonNull(nom, "nom ne peut pas etre null");
    }
}