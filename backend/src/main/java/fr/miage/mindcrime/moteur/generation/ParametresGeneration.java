package fr.miage.mindcrime.moteur.generation;

import java.util.Objects;

public record ParametresGeneration(
        String partieId,
        long seed,
        int nombreSuspects,
        int budgetNoeudsMax
) {
    public static final int NOMBRE_SUSPECTS_MIN = 3;
    public static final int NOMBRE_SUSPECTS_MAX = 6;
    public static final int BUDGET_DEFAUT = 5000;

    public ParametresGeneration {
        Objects.requireNonNull(partieId, "partieId ne peut pas etre null");
        if (partieId.isBlank()) {
            throw new IllegalArgumentException("partieId ne peut pas etre vide");
        }
        if (nombreSuspects < NOMBRE_SUSPECTS_MIN || nombreSuspects > NOMBRE_SUSPECTS_MAX) {
            throw new IllegalArgumentException("nombreSuspects doit etre entre 3 et 6");
        }
        if (budgetNoeudsMax <= 0) {
            throw new IllegalArgumentException("budgetNoeudsMax doit etre strictement positif");
        }
    }

    public ParametresGeneration(String partieId, long seed, int nombreSuspects) {
        this(partieId, seed, nombreSuspects, BUDGET_DEFAUT);
    }
}