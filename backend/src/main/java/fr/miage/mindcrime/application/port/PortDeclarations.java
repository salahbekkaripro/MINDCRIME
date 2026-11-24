package fr.miage.mindcrime.application.port;

import fr.miage.mindcrime.moteur.contraintes.InstantaneDeclarations;
import java.util.UUID;

/**
 * Contrat provisoire : retourne un instantané cohérent et entièrement chargé des seules
 * déclarations accessibles au lecteur. La révision doit dépendre du seul ensemble visible.
 * Une lecture impossible doit échouer, jamais retourner silencieusement une liste vide.
 */
public interface PortDeclarations {
    InstantaneDeclarations chargerAccessibles(UUID partieId, UUID lecteurId);

    static String perimetreLecteur(UUID lecteurId) {
        return "LECTEUR:" + java.util.Objects.requireNonNull(lecteurId, "lecteurId");
    }
}
