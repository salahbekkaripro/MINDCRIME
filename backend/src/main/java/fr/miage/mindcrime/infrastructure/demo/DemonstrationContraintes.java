package fr.miage.mindcrime.infrastructure.demo;

import fr.miage.mindcrime.application.alibi.AnalyserTemoignages;
import fr.miage.mindcrime.application.port.PortDeclarations;
import fr.miage.mindcrime.infrastructure.memoire.DepotDeclarationsEnMemoire;
import fr.miage.mindcrime.moteur.contraintes.*;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/** Démonstration locale avec données fictives ; aucun serveur ni partie multijoueur. */
public final class DemonstrationContraintes {
    private DemonstrationContraintes() { }

    public static void main(String[] args) {
        var partie = new UUID(0, 1);
        var lecteur = new UUID(0, 2);
        var auteur = new UUID(0, 3);
        var suspect = new UUID(0, 4);
        var cuisine = new UUID(0, 5);
        var jardin = new UUID(0, 6);
        var instant = Instant.parse("2026-11-24T20:00:00Z");
        var t1 = new Declaration(new UUID(0, 11), partie, auteur, suspect, cuisine, instant, NatureDeclaration.PRESENCE);
        var t2 = new Declaration(new UUID(0, 12), partie, auteur, suspect, jardin, instant, NatureDeclaration.PRESENCE);
        var repetition = new Declaration(new UUID(0, 13), partie, auteur, suspect, cuisine, instant, NatureDeclaration.PRESENCE);
        var depot = new DepotDeclarationsEnMemoire();
        var service = new AnalyserTemoignages(depot, new AnalyseurContraintesUnaires());
        depot.remplacerPourLecteur(lecteur, new InstantaneDeclarations(partie, "visible-1",
                PortDeclarations.perimetreLecteur(lecteur), List.of(t1), List.of()));
        System.out.println("Une présence : " + service.executer(partie, lecteur).verdict());
        depot.remplacerPourLecteur(lecteur, new InstantaneDeclarations(partie, "visible-2",
                PortDeclarations.perimetreLecteur(lecteur), List.of(t1, t2, repetition), List.of()));
        var resultat = service.executer(partie, lecteur);
        System.out.println("Deux lieux simultanés et une répétition : " + resultat.verdict());
        System.out.println("Conflits distincts : " + resultat.conflits().size());
        for (var conflit : resultat.conflits()) {
            System.out.println("Motif : " + conflit.motif());
            for (var cle : conflit.faits()) {
                System.out.println("Lieu " + cle.lieuId() + " ; sources " + resultat.faits().get(cle).sources());
            }
        }
    }
}
