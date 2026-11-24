package fr.miage.mindcrime.application.alibi;

import fr.miage.mindcrime.application.port.PortDossierTemoignages;
import fr.miage.mindcrime.application.port.ReferentielDossier;
import fr.miage.mindcrime.moteur.contraintes.Declaration;
import fr.miage.mindcrime.moteur.contraintes.InstantaneDeclarations;
import java.util.UUID;

/** Lecture via le port filtré ; une référence absente ou cachée produit la même erreur. */
public final class ConsulterTemoignages {
    private final PortDossierTemoignages depot;

    public ConsulterTemoignages(PortDossierTemoignages depot) { this.depot = depot; }
    public ReferentielDossier referentiel(UUID partieId, UUID lecteurId) { return depot.referentiel(partieId, lecteurId); }
    public InstantaneDeclarations liste(UUID partieId, UUID lecteurId) { return depot.chargerAccessibles(partieId, lecteurId); }
    public Declaration declaration(UUID partieId, UUID lecteurId, UUID declarationId) {
        return liste(partieId, lecteurId).declarations().stream().filter(d -> d.id().equals(declarationId))
                .findFirst().orElseThrow(ContexteInaccessible::new);
    }
}
