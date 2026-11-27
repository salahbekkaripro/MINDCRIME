package fr.miage.mindcrime.moteur.generation;

import fr.miage.mindcrime.moteur.modele.Creneau;
import fr.miage.mindcrime.moteur.modele.Enquete;
import fr.miage.mindcrime.moteur.modele.StatutUnicite;
import fr.miage.mindcrime.moteur.modele.Suspect;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GenerateurEnqueteBacktrackingTest {

    private final GenerateurEnqueteBacktracking generateur = new GenerateurEnqueteBacktracking();
    private final VerificateurUnicite verificateur = new VerificateurUnicite();

    @Test
    void genereEnqueteAvecCoupableUniqueSurIndicesVisibles() {
        ParametresGeneration params = new ParametresGeneration("PARTIE-1", 42L, 5);
        ResultatGeneration resultat = generateur.generer(params);

        assertEquals(StatutUnicite.COUPABLE_UNIQUE, resultat.statut());
        assertNotNull(resultat.enquete());

        Enquete enquete = resultat.enquete();
        Set<String> compatibles = verificateur.suspectsCompatibles(
                enquete.suspects(),
                enquete.crime(),
                enquete.indicesVisibles(),
                generateur.grapheLieuxParDefaut()
        );

        assertEquals(Set.of(enquete.veriteTerrain().coupableId()), compatibles);
    }

    @Test
    void estStrictementReproductibleAMemeGraine() {
        ParametresGeneration params = new ParametresGeneration("PARTIE-1", 12345L, 4);

        ResultatGeneration r1 = generateur.generer(params);
        ResultatGeneration r2 = generateur.generer(params);

        assertEquals(r1.statut(), r2.statut());
        assertEquals(r1.enquete(), r2.enquete());
    }

    @Test
    void respecteContinuiteSpatioTemporelle() {
        ResultatGeneration resultat = generateur.generer(new ParametresGeneration("PARTIE-2", 99L, 6));
        assertEquals(StatutUnicite.COUPABLE_UNIQUE, resultat.statut());

        Enquete enquete = resultat.enquete();
        Map<String, Set<String>> graphe = generateur.grapheLieuxParDefaut();

        for (Suspect suspect : enquete.suspects()) {
            for (int i = 1; i < enquete.creneaux().size(); i++) {
                Creneau prec = enquete.creneaux().get(i - 1);
                Creneau curr = enquete.creneaux().get(i);
                String lieuPrec = enquete.veriteTerrain().lieuDe(suspect.id(), prec);
                String lieuCurr = enquete.veriteTerrain().lieuDe(suspect.id(), curr);

                boolean valide = lieuPrec.equals(lieuCurr)
                        || graphe.getOrDefault(lieuPrec, Set.of()).contains(lieuCurr);
                assertTrue(valide);
            }
        }
    }

    @Test
    void retourneRechercheIncompleteSiBudgetSature() {
        ParametresGeneration params = new ParametresGeneration("PARTIE-3", 42L, 5, 1);
        ResultatGeneration resultat = generateur.generer(params);

        assertEquals(StatutUnicite.RECHERCHE_INCOMPLETE, resultat.statut());
        assertNull(resultat.enquete());
    }

    @Test
    void rejetteParametresInvalides() {
        assertThrows(IllegalArgumentException.class, () -> new ParametresGeneration("P1", 1L, 2));
        assertThrows(IllegalArgumentException.class, () -> new ParametresGeneration("P1", 1L, 7));
        assertThrows(IllegalArgumentException.class, () -> new ParametresGeneration(" ", 1L, 4));
    }
}