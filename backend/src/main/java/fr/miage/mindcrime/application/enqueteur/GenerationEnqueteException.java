package fr.miage.mindcrime.application.enqueteur;

import fr.miage.mindcrime.moteur.modele.StatutUnicite;

public class GenerationEnqueteException extends RuntimeException {
    private final StatutUnicite statut;

    public GenerationEnqueteException(StatutUnicite statut) {
        super("Generation impossible : " + statut);
        this.statut = statut;
    }

    public StatutUnicite getStatut() {
        return statut;
    }
}