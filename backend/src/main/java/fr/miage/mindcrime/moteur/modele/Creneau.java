package fr.miage.mindcrime.moteur.modele;

import java.util.Objects;

public record Creneau(int ordre, String libelle) implements Comparable<Creneau> {
    public Creneau {
        if (ordre < 0) {
            throw new IllegalArgumentException("ordre doit etre positif ou nul");
        }
        Objects.requireNonNull(libelle, "libelle ne peut pas etre null");
    }

    @Override
    public int compareTo(Creneau autre) {
        return Integer.compare(this.ordre, autre.ordre);
    }
}