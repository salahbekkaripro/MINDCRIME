package fr.miage.mindcrime.moteur.modele;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public record VeriteTerrain(String coupableId, Map<String, Map<Creneau, String>> presences) {
    public VeriteTerrain {
        Objects.requireNonNull(coupableId, "coupableId ne peut pas etre null");
        Objects.requireNonNull(presences, "presences ne peut pas etre null");
        Map<String, Map<Creneau, String>> copie = new HashMap<>();
        for (Map.Entry<String, Map<Creneau, String>> entree : presences.entrySet()) {
            copie.put(entree.getKey(), Collections.unmodifiableMap(new HashMap<>(entree.getValue())));
        }
        presences = Collections.unmodifiableMap(copie);
    }

    public String lieuDe(String suspectId, Creneau creneau) {
        Map<Creneau, String> emploiDuTemps = presences.get(suspectId);
        return emploiDuTemps == null ? null : emploiDuTemps.get(creneau);
    }
}