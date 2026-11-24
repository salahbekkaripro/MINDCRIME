package fr.miage.mindcrime.moteur.contraintes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/** Domaine symbolique interne : AUTRE_LIEU reste possible tant qu'aucune présence ne l'exclut. */
final class DomainePresence {
    private final Map<UUID, CleFait> presences = new HashMap<>();
    private final Map<UUID, CleFait> absences = new HashMap<>();

    void propager(CleFait fait) {
        var index = fait.nature() == NatureDeclaration.PRESENCE ? presences : absences;
        index.put(fait.lieuId(), fait);
    }

    void ajouterConflitsDans(Set<Conflit> conflits) {
        var presents = new ArrayList<>(presences.values());
        for (int i = 0; i < presents.size(); i++) {
            var a = presents.get(i);
            for (int j = i + 1; j < presents.size(); j++) {
                conflits.add(new Conflit(MotifConflit.LIEUX_DISTINCTS, Set.of(a, presents.get(j))));
            }
            var absence = absences.get(a.lieuId());
            if (absence != null) {
                conflits.add(new Conflit(MotifConflit.PRESENCE_ABSENCE, Set.of(a, absence)));
            }
        }
    }
}
