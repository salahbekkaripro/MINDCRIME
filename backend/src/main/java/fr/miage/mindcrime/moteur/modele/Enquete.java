package fr.miage.mindcrime.moteur.modele;

import java.util.List;
import java.util.Objects;

public record Enquete(
        String id,
        String partieId,
        long seed,
        Crime crime,
        List<Suspect> suspects,
        List<Lieu> lieux,
        List<Creneau> creneaux,
        List<Indice> indices,
        VeriteTerrain veriteTerrain,
        StatutUnicite statutUnicite
) {
    public Enquete {
        Objects.requireNonNull(id, "id ne peut pas etre null");
        Objects.requireNonNull(partieId, "partieId ne peut pas etre null");
        Objects.requireNonNull(crime, "crime ne peut pas etre null");
        suspects = List.copyOf(Objects.requireNonNull(suspects, "suspects ne peut pas etre null"));
        lieux = List.copyOf(Objects.requireNonNull(lieux, "lieux ne peut pas etre null"));
        creneaux = List.copyOf(Objects.requireNonNull(creneaux, "creneaux ne peut pas etre null"));
        indices = List.copyOf(Objects.requireNonNull(indices, "indices ne peut pas etre null"));
        Objects.requireNonNull(veriteTerrain, "veriteTerrain ne peut pas etre null");
        Objects.requireNonNull(statutUnicite, "statutUnicite ne peut pas etre null");
    }

    public List<Indice> indicesVisibles() {
        return indices.stream().filter(Indice::visible).toList();
    }
}