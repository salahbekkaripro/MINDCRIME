package fr.miage.mindcrime.application.alibi;

import fr.miage.mindcrime.application.port.PortDeclarations;
import fr.miage.mindcrime.moteur.contraintes.AnalyseurContraintesUnaires;
import fr.miage.mindcrime.moteur.contraintes.ErreurContratAnalyse;
import fr.miage.mindcrime.moteur.contraintes.ResultatAnalyse;
import java.util.Objects;
import java.util.UUID;

/** Lecture par port puis analyse ; authentification/référentiels restent à intégrer. */
public final class AnalyserTemoignages {
    private final PortDeclarations declarations;
    private final AnalyseurContraintesUnaires analyseur;

    public AnalyserTemoignages(PortDeclarations declarations, AnalyseurContraintesUnaires analyseur) {
        this.declarations = Objects.requireNonNull(declarations, "declarations");
        this.analyseur = Objects.requireNonNull(analyseur, "analyseur");
    }

    public ResultatAnalyse executer(UUID partieId, UUID lecteurId) {
        Objects.requireNonNull(partieId, "partieId");
        Objects.requireNonNull(lecteurId, "lecteurId");
        var instantane = Objects.requireNonNull(declarations.chargerAccessibles(partieId, lecteurId));
        if (!partieId.equals(instantane.partieId())
                || !PortDeclarations.perimetreLecteur(lecteurId).equals(instantane.perimetre())) {
            throw new ErreurContratAnalyse("Le port retourne un contexte différent du contexte demandé.");
        }
        return analyseur.analyser(instantane);
    }
}
