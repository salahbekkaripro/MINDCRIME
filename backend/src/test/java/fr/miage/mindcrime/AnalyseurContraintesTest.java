package fr.miage.mindcrime;

import fr.miage.mindcrime.moteur.contraintes.*;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import static fr.miage.mindcrime.JeuDeclarations.*;
import static org.junit.jupiter.api.Assertions.*;

class AnalyseurContraintesTest {
    private final AnalyseurContraintesUnaires analyseur = new AnalyseurContraintesUnaires();

    private void verifier(List<Declaration> declarations, List<DeclarationNonExploitable> incompletes,
                          VerdictAnalyse verdict, CompletudeAnalyse completude, int conflits) {
        var resultat = analyseur.analyser(instantane(declarations, incompletes));
        assertEquals(verdict, resultat.verdict());
        assertEquals(completude, resultat.completude());
        assertEquals(conflits, resultat.conflits().size());
        resultat.conflits().forEach(conflit -> conflit.faits().forEach(cle ->
                assertFalse(resultat.faits().get(cle).sources().isEmpty())));
    }

    @TestFactory
    Stream<DynamicTest> casAcceptationC01AC10() {
        var t1 = presence(11, CUISINE);
        var t2 = presence(12, JARDIN);
        var autreHeure = new Declaration(id(12), PARTIE, AUTEUR, SUSPECT, JARDIN,
                HEURE.plusSeconds(3600), NatureDeclaration.PRESENCE);
        var autreSuspect = new Declaration(id(12), PARTIE, AUTEUR, id(99), JARDIN,
                HEURE, NatureDeclaration.PRESENCE);
        return Stream.of(
            DynamicTest.dynamicTest("C01 deux lieux simultanés", () -> verifier(List.of(t1, t2), List.of(),
                    VerdictAnalyse.CONTRADICTION, CompletudeAnalyse.COMPLETE, 1)),
            DynamicTest.dynamicTest("C02 présence et absence", () -> verifier(List.of(t1, absence(12, CUISINE)), List.of(),
                    VerdictAnalyse.CONTRADICTION, CompletudeAnalyse.COMPLETE, 1)),
            DynamicTest.dynamicTest("C03 heures différentes", () -> verifier(List.of(t1, autreHeure), List.of(),
                    VerdictAnalyse.AUCUNE_CONTRADICTION_DETECTEE, CompletudeAnalyse.COMPLETE, 0)),
            DynamicTest.dynamicTest("C04 suspects différents", () -> verifier(List.of(t1, autreSuspect), List.of(),
                    VerdictAnalyse.AUCUNE_CONTRADICTION_DETECTEE, CompletudeAnalyse.COMPLETE, 0)),
            DynamicTest.dynamicTest("C05 répétition identique", () -> verifier(List.of(t1, presence(12, CUISINE)), List.of(),
                    VerdictAnalyse.AUCUNE_CONTRADICTION_DETECTEE, CompletudeAnalyse.COMPLETE, 0)),
            DynamicTest.dynamicTest("C06 heure manquante", () -> verifier(List.of(t1), List.of(incomplete(12)),
                    VerdictAnalyse.AUCUNE_CONTRADICTION_DETECTEE, CompletudeAnalyse.INCOMPLETE, 0)),
            DynamicTest.dynamicTest("C07 absence ailleurs", () -> verifier(List.of(t1, absence(12, JARDIN)), List.of(),
                    VerdictAnalyse.AUCUNE_CONTRADICTION_DETECTEE, CompletudeAnalyse.COMPLETE, 0)),
            DynamicTest.dynamicTest("C08 parties indépendantes", () -> {
                verifier(List.of(t1), List.of(), VerdictAnalyse.AUCUNE_CONTRADICTION_DETECTEE, CompletudeAnalyse.COMPLETE, 0);
                var autre = new Declaration(id(12), id(99), AUTEUR, SUSPECT, JARDIN, HEURE, NatureDeclaration.PRESENCE);
                var resultat = analyseur.analyser(new InstantaneDeclarations(id(99), "v1", "INTERNE", List.of(autre), List.of()));
                assertTrue(resultat.conflits().isEmpty());
            }),
            DynamicTest.dynamicTest("C09 absences dans tous les lieux connus", () -> verifier(
                    List.of(absence(11, CUISINE), absence(12, JARDIN), absence(13, SALON)), List.of(),
                    VerdictAnalyse.AUCUNE_CONTRADICTION_DETECTEE, CompletudeAnalyse.COMPLETE, 0)),
            DynamicTest.dynamicTest("C10 conflit et déclaration incomplète", () -> verifier(List.of(t1, t2), List.of(incomplete(13)),
                    VerdictAnalyse.CONTRADICTION, CompletudeAnalyse.INCOMPLETE, 1))
        );
    }

    @Test void ensembleVideNeProuveAucunePresence() {
        verifier(List.of(), List.of(), VerdictAnalyse.AUCUNE_CONTRADICTION_DETECTEE, CompletudeAnalyse.COMPLETE, 0);
    }

    @Test void aucuneDonneeExploitableEstNonVerifiable() {
        verifier(List.of(), List.of(incomplete(11)), VerdictAnalyse.NON_VERIFIABLE, CompletudeAnalyse.INCOMPLETE, 0);
    }

    @Test void repetitionsConserventLesSourcesSansMultiplierLesConflits() {
        var a = presence(11, CUISINE);
        var auteurDifferent = new Declaration(id(13), PARTIE, id(77), SUSPECT, CUISINE, HEURE, NatureDeclaration.PRESENCE);
        var resultat = analyseur.analyser(instantane(List.of(a, a, presence(12, JARDIN), auteurDifferent)));
        assertEquals(1, resultat.conflits().size());
        assertEquals(Set.of(id(11), id(13)), resultat.faits().get(a.cleFait()).sources());
        assertEquals(2, resultat.faits().size());
    }

    @Test void neSArretePasAuPremierConflit() {
        var resultat = analyseur.analyser(instantane(List.of(presence(11, CUISINE), presence(12, JARDIN),
                presence(13, SALON), absence(14, CUISINE))));
        assertEquals(4, resultat.conflits().size());
        assertEquals(3, resultat.conflits().stream().filter(c -> c.motif() == MotifConflit.LIEUX_DISTINCTS).count());
    }

    @Test void conflitDeLieuxReferenceExactementLesFaitsEtLeursSources() {
        var cuisine = presence(11, CUISINE);
        var jardin = presence(12, JARDIN);
        var resultat = analyseur.analyser(instantane(List.of(cuisine, jardin, presence(13, CUISINE), absence(14, SALON))));
        assertEquals(1, resultat.conflits().size());
        var conflit = resultat.conflits().iterator().next();
        assertEquals(MotifConflit.LIEUX_DISTINCTS, conflit.motif());
        assertEquals(Set.of(cuisine.cleFait(), jardin.cleFait()), conflit.faits());
        assertEquals(Set.of(id(11), id(13)), resultat.faits().get(cuisine.cleFait()).sources());
        assertEquals(Set.of(id(12)), resultat.faits().get(jardin.cleFait()).sources());
    }

    @Test void conflitPresenceAbsenceReferenceExactementLesDeclarationsConcernees() {
        var presence = presence(11, CUISINE);
        var absence = absence(12, CUISINE);
        var resultat = analyseur.analyser(instantane(List.of(presence, absence, absence(13, JARDIN))));
        assertEquals(1, resultat.conflits().size());
        var conflit = resultat.conflits().iterator().next();
        assertEquals(MotifConflit.PRESENCE_ABSENCE, conflit.motif());
        assertEquals(Set.of(presence.cleFait(), absence.cleFait()), conflit.faits());
        assertEquals(Set.of(id(11)), resultat.faits().get(presence.cleFait()).sources());
        assertEquals(Set.of(id(12)), resultat.faits().get(absence.cleFait()).sources());
    }

    @Test void informationsInsuffisantesConserventLeursReferencesEtRaisonsSansCreerDeConflit() {
        var sansLieu = new DeclarationNonExploitable(id(20), Set.of("lieu manquant"));
        var sansHeure = incomplete(21);
        var resultat = analyseur.analyser(instantane(List.of(presence(11, CUISINE)), List.of(sansLieu, sansHeure)));
        assertEquals(VerdictAnalyse.AUCUNE_CONTRADICTION_DETECTEE, resultat.verdict());
        assertEquals(CompletudeAnalyse.INCOMPLETE, resultat.completude());
        assertTrue(resultat.conflits().isEmpty());
        assertEquals(Set.of(sansLieu, sansHeure), Set.copyOf(resultat.nonExploitables()));
        assertEquals(Set.of(presence(11, CUISINE).cleFait()), resultat.faits().keySet());
    }

    @Test void deuxInstantsDistantsDUneSecondeNeSontPasFusionnes() {
        var ensuite = new Declaration(id(12), PARTIE, AUTEUR, SUSPECT, JARDIN,
                HEURE.plusSeconds(1), NatureDeclaration.PRESENCE);
        verifier(List.of(presence(11, CUISINE), ensuite), List.of(),
                VerdictAnalyse.AUCUNE_CONTRADICTION_DETECTEE, CompletudeAnalyse.COMPLETE, 0);
    }

    @Test void permutationConserveFaitsConflitsEtCompletude() {
        var entrees = new ArrayList<>(List.of(presence(11, CUISINE), presence(12, JARDIN),
                presence(13, CUISINE), absence(14, JARDIN)));
        var a = analyseur.analyser(instantane(entrees, List.of(incomplete(20))));
        Collections.reverse(entrees);
        var b = analyseur.analyser(instantane(entrees, List.of(incomplete(20))));
        assertEquals(a, b);
    }

    @Test void les120PermutationsConserventVerdictFaitsConflitsEtSources() {
        var declarations = new ArrayList<>(List.of(presence(11, CUISINE), presence(12, JARDIN),
                presence(13, CUISINE), absence(14, JARDIN), absence(15, SALON)));
        var reference = analyseur.analyser(instantane(declarations, List.of(incomplete(20), incomplete(21))));
        assertEquals(120, verifierPermutations(declarations, 0, reference));
    }

    private int verifierPermutations(List<Declaration> declarations, int position, ResultatAnalyse reference) {
        if (position == declarations.size()) {
            var resultat = analyseur.analyser(instantane(declarations, List.of(incomplete(21), incomplete(20))));
            assertEquals(reference.verdict(), resultat.verdict());
            assertEquals(reference.completude(), resultat.completude());
            assertEquals(reference.faits(), resultat.faits());
            assertEquals(reference.conflits(), resultat.conflits());
            assertEquals(Set.copyOf(reference.nonExploitables()), Set.copyOf(resultat.nonExploitables()));
            return 1;
        }
        int total = 0;
        for (int i = position; i < declarations.size(); i++) {
            Collections.swap(declarations, position, i);
            total += verifierPermutations(declarations, position + 1, reference);
            Collections.swap(declarations, position, i);
        }
        return total;
    }

    @Test void jeuxExhaustifsConcordentAvecIntersectionDeDomainesEtOracleParPaires() {
        var lieux = List.of(CUISINE, JARDIN, SALON);
        int total = 0;
        for (int taille = 0; taille <= 4; taille++) {
            int combinaisons = (int) Math.pow(7, taille);
            for (int code = 0; code < combinaisons; code++) {
                var declarations = new ArrayList<Declaration>();
                var incompletes = new ArrayList<DeclarationNonExploitable>();
                var domaine = new HashSet<>(lieux);
                domaine.add(id(999)); // AUTRE_LIEU : les absences ne peuvent pas l'exclure.
                int reste = code;
                for (int position = 0; position < taille; position++) {
                    int valeur = reste % 7;
                    reste /= 7;
                    if (valeur == 6) {
                        incompletes.add(incomplete(20 + position));
                    } else {
                        var lieu = lieux.get(valeur % 3);
                        var nature = valeur < 3 ? NatureDeclaration.PRESENCE : NatureDeclaration.ABSENCE;
                        declarations.add(declaration(20 + position, lieu, nature));
                        if (nature == NatureDeclaration.PRESENCE) domaine.retainAll(Set.of(lieu));
                        else domaine.remove(lieu);
                    }
                }
                var resultat = analyseur.analyser(instantane(declarations, incompletes));
                assertEquals(domaine.isEmpty(), resultat.verdict() == VerdictAnalyse.CONTRADICTION,
                        "taille=" + taille + ", code=" + code);
                Set<Conflit> oracle = new HashSet<>();
                for (int i = 0; i < declarations.size(); i++) {
                    for (int j = i + 1; j < declarations.size(); j++) {
                        var a = declarations.get(i);
                        var b = declarations.get(j);
                        if (a.nature() == NatureDeclaration.PRESENCE && b.nature() == NatureDeclaration.PRESENCE
                                && !a.lieuId().equals(b.lieuId())) {
                            oracle.add(new Conflit(MotifConflit.LIEUX_DISTINCTS, Set.of(a.cleFait(), b.cleFait())));
                        } else if (a.nature() != b.nature() && a.lieuId().equals(b.lieuId())) {
                            oracle.add(new Conflit(MotifConflit.PRESENCE_ABSENCE, Set.of(a.cleFait(), b.cleFait())));
                        }
                    }
                }
                assertEquals(oracle, resultat.conflits());
                assertEquals(incompletes.isEmpty() ? CompletudeAnalyse.COMPLETE : CompletudeAnalyse.INCOMPLETE,
                        resultat.completude());
                total++;
            }
        }
        assertEquals(2801, total);
    }
}
