package fr.miage.mindcrime.application.port;

import fr.miage.mindcrime.moteur.contraintes.Declaration;
import fr.miage.mindcrime.moteur.contraintes.InstantaneDeclarations;
import java.util.UUID;
import java.util.function.Function;

public interface PortDossierTemoignages extends PortDeclarations {
    ReferentielDossier referentiel(UUID partieId, UUID lecteurId);

    /** Transaction sérialisée par partie ; toute exception annule l'écriture. */
    <T> T dansTransaction(UUID partieId, UUID lecteurId, Function<Ecriture, T> operation);

    interface Ecriture {
        void verifierReferences(UUID suspectId, UUID lieuId);
        void enregistrer(Declaration declaration);
        InstantaneDeclarations charger();
    }
}
