package fr.miage.mindcrime;

import fr.miage.mindcrime.moteur.contraintes.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;
import static fr.miage.mindcrime.JeuDeclarations.*;
import static org.junit.jupiter.api.Assertions.*;

class ContratsAnalyseTest {
    private final AnalyseurContraintesUnaires analyseur = new AnalyseurContraintesUnaires();

    @Test void identifiantReutiliseAvecAutreContenuEstRefuse() {
        assertThrows(ErreurContratAnalyse.class, () -> analyseur.analyser(
                instantane(List.of(presence(11, CUISINE), presence(11, JARDIN)))));
    }

    @Test void identifiantCompletEtIncompletNePeutPasEtreConfondus() {
        assertThrows(ErreurContratAnalyse.class, () -> analyseur.analyser(
                instantane(List.of(presence(11, CUISINE)), List.of(incomplete(11)))));
    }

    @Test void deuxVersionsIncompletesDuMemeIdentifiantSontRefusees() {
        assertThrows(ErreurContratAnalyse.class, () -> analyseur.analyser(instantane(List.of(),
                List.of(incomplete(11), new DeclarationNonExploitable(id(11), Set.of("lieu manquant"))))));
    }

    @Test void repetitionsIdentiquesIncompletesSontRegroupees() {
        var resultat = analyseur.analyser(instantane(List.of(), List.of(incomplete(11), incomplete(11))));
        assertEquals(List.of(incomplete(11)), resultat.nonExploitables());
    }

    @Test void melangeDePartiesEstUneErreurPasUnConflit() {
        var autre = new Declaration(id(12), id(99), AUTEUR, SUSPECT, JARDIN, HEURE, NatureDeclaration.PRESENCE);
        assertThrows(ErreurContratAnalyse.class, () -> analyseur.analyser(instantane(List.of(presence(11, CUISINE), autre))));
    }

    @Test void fractionsDeSecondeNeSontPasArrondies() {
        assertThrows(ErreurContratAnalyse.class, () -> new Declaration(id(11), PARTIE, AUTEUR, SUSPECT,
                CUISINE, HEURE.plusNanos(1), NatureDeclaration.PRESENCE));
    }

    @TestFactory Stream<DynamicTest> chaqueChampObligatoireManquantEstRejete() {
        record Cas(String champ, Supplier<Declaration> construire) { }
        return Stream.of(
                new Cas("identifiant", () -> new Declaration(null, PARTIE, AUTEUR, SUSPECT, CUISINE, HEURE, NatureDeclaration.PRESENCE)),
                new Cas("partie", () -> new Declaration(id(11), null, AUTEUR, SUSPECT, CUISINE, HEURE, NatureDeclaration.PRESENCE)),
                new Cas("auteur", () -> new Declaration(id(11), PARTIE, null, SUSPECT, CUISINE, HEURE, NatureDeclaration.PRESENCE)),
                new Cas("suspect concerné", () -> new Declaration(id(11), PARTIE, AUTEUR, null, CUISINE, HEURE, NatureDeclaration.PRESENCE)),
                new Cas("lieu", () -> new Declaration(id(11), PARTIE, AUTEUR, SUSPECT, null, HEURE, NatureDeclaration.PRESENCE)),
                new Cas("instant", () -> new Declaration(id(11), PARTIE, AUTEUR, SUSPECT, CUISINE, null, NatureDeclaration.PRESENCE)),
                new Cas("nature", () -> new Declaration(id(11), PARTIE, AUTEUR, SUSPECT, CUISINE, HEURE, null))
        ).map(cas -> DynamicTest.dynamicTest(cas.champ() + " manquant : rejet, pas de verdict métier",
                () -> assertThrows(NullPointerException.class, () -> cas.construire().get())));
    }

    @Test void limiteCompteAussiLesDonneesNonExploitablesEtNeTronquePas() {
        var limiteUn = new AnalyseurContraintesUnaires(1);
        assertThrows(LimiteAnalyseDepassee.class, () -> limiteUn.analyser(
                instantane(List.of(presence(11, CUISINE)), List.of(incomplete(12)))));
        assertEquals(1, limiteUn.analyser(instantane(List.of(presence(11, CUISINE)))).faits().size());
    }

    @Test void entreeEtSortiesSontImmuablesYComprisLesSources() {
        var liste = new ArrayList<>(List.of(presence(11, CUISINE), presence(12, JARDIN)));
        var instantane = instantane(liste);
        liste.clear();
        var resultat = analyseur.analyser(instantane);
        assertEquals(2, resultat.faits().size());
        assertThrows(UnsupportedOperationException.class, () -> instantane.declarations().clear());
        assertThrows(UnsupportedOperationException.class, () -> resultat.faits().clear());
        assertThrows(UnsupportedOperationException.class, () -> resultat.conflits().clear());
        assertThrows(UnsupportedOperationException.class, () -> resultat.faits().values().iterator().next().sources().clear());
        assertThrows(UnsupportedOperationException.class, () -> resultat.conflits().iterator().next().faits().clear());
    }

    @Test void reutiliserAnalyseurNeConservePasLesConflitsPrecedents() {
        analyseur.analyser(instantane(List.of(presence(11, CUISINE), presence(12, JARDIN))));
        assertEquals(VerdictAnalyse.AUCUNE_CONTRADICTION_DETECTEE, analyseur.analyser(instantane(List.of())).verdict());
    }
}
