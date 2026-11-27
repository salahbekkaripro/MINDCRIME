package fr.miage.mindcrime.moteur.generation;

import fr.miage.mindcrime.moteur.modele.Crime;
import fr.miage.mindcrime.moteur.modele.Indice;
import fr.miage.mindcrime.moteur.modele.StatutUnicite;
import fr.miage.mindcrime.moteur.modele.Suspect;
import fr.miage.mindcrime.moteur.modele.TypeIndice;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class VerificateurUnicite {

    public Set<String> suspectsCompatibles(
            List<Suspect> suspects,
            Crime crime,
            List<Indice> indicesVisibles,
            Map<String, Set<String>> grapheLieux
    ) {
        Set<String> compatibles = new LinkedHashSet<>();
        for (Suspect suspect : suspects) {
            if (estCompatible(suspect.id(), crime, indicesVisibles, grapheLieux)) {
                compatibles.add(suspect.id());
            }
        }
        return compatibles;
    }

    public StatutUnicite verifier(
            List<Suspect> suspects,
            Crime crime,
            List<Indice> indicesVisibles,
            Map<String, Set<String>> grapheLieux,
            String coupableAttendu
    ) {
        Set<String> compatibles = suspectsCompatibles(suspects, crime, indicesVisibles, grapheLieux);
        if (compatibles.isEmpty()) {
            return StatutUnicite.AUCUNE_SOLUTION;
        }
        if (compatibles.size() == 1) {
            return compatibles.contains(coupableAttendu)
                    ? StatutUnicite.COUPABLE_UNIQUE
                    : StatutUnicite.AUCUNE_SOLUTION;
        }
        return StatutUnicite.SOLUTIONS_MULTIPLES;
    }

    private boolean estCompatible(
            String suspectId,
            Crime crime,
            List<Indice> indicesVisibles,
            Map<String, Set<String>> grapheLieux
    ) {
        for (Indice indice : indicesVisibles) {
            if (!indice.visible() || !indice.suspectId().equals(suspectId)) {
                continue;
            }
            if (elimineSuspect(indice, crime, grapheLieux)) {
                return false;
            }
        }
        return true;
    }

    public boolean elimineSuspect(
            Indice indice,
            Crime crime,
            Map<String, Set<String>> grapheLieux
    ) {
        int ecartCreneau = Math.abs(indice.creneau().ordre() - crime.creneau().ordre());
        if (ecartCreneau == 0) {
            if ((indice.type() == TypeIndice.ALIBI_CONFIRME || indice.type() == TypeIndice.PRESENCE_LIEU)
                    && !indice.lieuId().equals(crime.lieuId())) {
                return true;
            }
            if (indice.type() == TypeIndice.ABSENCE_LIEU && indice.lieuId().equals(crime.lieuId())) {
                return true;
            }
        } else if (ecartCreneau == 1
                && (indice.type() == TypeIndice.PRESENCE_LIEU || indice.type() == TypeIndice.ALIBI_CONFIRME)) {
            boolean memeLieu = indice.lieuId().equals(crime.lieuId());
            boolean adjacent = grapheLieux.getOrDefault(crime.lieuId(), Set.of()).contains(indice.lieuId());
            if (!memeLieu && !adjacent) {
                return true;
            }
        }
        return false;
    }
}