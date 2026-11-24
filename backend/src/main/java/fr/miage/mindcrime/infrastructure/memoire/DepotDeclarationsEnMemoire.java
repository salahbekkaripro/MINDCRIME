package fr.miage.mindcrime.infrastructure.memoire;

import fr.miage.mindcrime.application.port.PortDeclarations;
import fr.miage.mindcrime.moteur.contraintes.ErreurContratAnalyse;
import fr.miage.mindcrime.moteur.contraintes.InstantaneDeclarations;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Adaptateur PROVISOIRE de démonstration/tests, sans BD ni authentification.
 * Les ensembles visibles et leur révision sont préparés par l'appelant de confiance.
 * Ne remplace ni les permissions D4 ni la transaction de soumission future.
 */
public final class DepotDeclarationsEnMemoire implements PortDeclarations {
    private record Contexte(UUID partieId, UUID lecteurId) { }
    private final Map<Contexte, InstantaneDeclarations> instantanes = new ConcurrentHashMap<>();

    public void remplacerPourLecteur(UUID lecteurId, InstantaneDeclarations instantane) {
        Objects.requireNonNull(instantane, "instantane");
        if (!PortDeclarations.perimetreLecteur(lecteurId).equals(instantane.perimetre())) {
            throw new ErreurContratAnalyse("L'instantané ne correspond pas au lecteur.");
        }
        instantanes.put(new Contexte(instantane.partieId(), lecteurId), instantane);
    }

    @Override
    public InstantaneDeclarations chargerAccessibles(UUID partieId, UUID lecteurId) {
        var contexte = new Contexte(Objects.requireNonNull(partieId), Objects.requireNonNull(lecteurId));
        var instantane = instantanes.get(contexte);
        if (instantane == null) {
            throw new NoSuchElementException("Aucun instantané accessible pour ce contexte.");
        }
        return instantane;
    }
}
