package fr.miage.mindcrime;

import fr.miage.mindcrime.application.alibi.AnalyserTemoignages;
import fr.miage.mindcrime.application.port.PortDeclarations;
import fr.miage.mindcrime.infrastructure.memoire.DepotDeclarationsEnMemoire;
import fr.miage.mindcrime.moteur.contraintes.*;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.NoSuchElementException;
import static fr.miage.mindcrime.JeuDeclarations.*;
import static org.junit.jupiter.api.Assertions.*;

class AnalyserTemoignagesTest {
    @Test void chargeParLePortEtDetecteLeConflit() {
        var depot = new DepotDeclarationsEnMemoire();
        depot.remplacerPourLecteur(LECTEUR, instantane(List.of(presence(11, CUISINE), presence(12, JARDIN))));
        var service = new AnalyserTemoignages(depot, new AnalyseurContraintesUnaires());
        assertEquals(VerdictAnalyse.CONTRADICTION, service.executer(PARTIE, LECTEUR).verdict());
    }

    @Test void contexteAbsentNeDevientPasUnResultatVideCompatible() {
        var service = new AnalyserTemoignages(new DepotDeclarationsEnMemoire(), new AnalyseurContraintesUnaires());
        assertThrows(NoSuchElementException.class, () -> service.executer(PARTIE, LECTEUR));
    }

    @Test void portAvecMauvaisePartieEstRefuse() {
        PortDeclarations port = (partie, lecteur) -> new InstantaneDeclarations(id(99), "v1",
                PortDeclarations.perimetreLecteur(lecteur), List.of(), List.of());
        assertThrows(ErreurContratAnalyse.class, () -> new AnalyserTemoignages(port,
                new AnalyseurContraintesUnaires()).executer(PARTIE, LECTEUR));
    }

    @Test void portAvecAutreLecteurEstRefuse() {
        PortDeclarations port = (partie, lecteur) -> new InstantaneDeclarations(partie, "v1",
                PortDeclarations.perimetreLecteur(id(99)), List.of(), List.of());
        assertThrows(ErreurContratAnalyse.class, () -> new AnalyserTemoignages(port,
                new AnalyseurContraintesUnaires()).executer(PARTIE, LECTEUR));
    }

    @Test void changerUnEnsembleCacheNeChangePasLaReponseDuLecteur() {
        var depot = new DepotDeclarationsEnMemoire();
        depot.remplacerPourLecteur(LECTEUR, instantane(List.of(presence(11, CUISINE))));
        var service = new AnalyserTemoignages(depot, new AnalyseurContraintesUnaires());
        var avant = service.executer(PARTIE, LECTEUR);
        var autreLecteur = id(99);
        depot.remplacerPourLecteur(autreLecteur, new InstantaneDeclarations(PARTIE, "prive-2",
                PortDeclarations.perimetreLecteur(autreLecteur), List.of(presence(11, CUISINE), presence(12, JARDIN)), List.of()));
        assertEquals(VerdictAnalyse.CONTRADICTION, service.executer(PARTIE, autreLecteur).verdict());
        assertEquals(avant, service.executer(PARTIE, LECTEUR));
    }

    @Test void erreurDeStockageEstPropageeSansInventerUnVerdict() {
        PortDeclarations port = (partie, lecteur) -> { throw new IllegalStateException("Stockage indisponible"); };
        assertThrows(IllegalStateException.class, () -> new AnalyserTemoignages(port,
                new AnalyseurContraintesUnaires()).executer(PARTIE, LECTEUR));
    }
}
