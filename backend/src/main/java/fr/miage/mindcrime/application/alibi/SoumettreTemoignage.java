package fr.miage.mindcrime.application.alibi;

import fr.miage.mindcrime.application.port.PortDossierTemoignages;
import fr.miage.mindcrime.moteur.contraintes.AnalyseurContraintesUnaires;
import fr.miage.mindcrime.moteur.contraintes.Declaration;
import java.util.Objects;
import java.util.UUID;

/** L'auteur provient du contexte serveur, jamais d'un auteurId soumis par le client. */
public final class SoumettreTemoignage {
    private final PortDossierTemoignages dossier;
    private final AnalyseurContraintesUnaires analyseur;

    public SoumettreTemoignage(PortDossierTemoignages dossier, AnalyseurContraintesUnaires analyseur) {
        this.dossier = Objects.requireNonNull(dossier);
        this.analyseur = Objects.requireNonNull(analyseur);
    }

    public TemoignageSoumis executer(UUID partieId, UUID auteurId, CommandeTemoignage commande) {
        Objects.requireNonNull(commande);
        return dossier.dansTransaction(partieId, auteurId, ecriture -> {
            ecriture.verifierReferences(commande.suspectConcerneId(), commande.lieuId());
            var declaration = new Declaration(UUID.randomUUID(), partieId, auteurId, commande.suspectConcerneId(),
                    commande.lieuId(), commande.instant(), commande.nature());
            ecriture.enregistrer(declaration);
            var analyse = analyseur.analyser(ecriture.charger());
            return new TemoignageSoumis(declaration, analyse);
        });
    }
}
