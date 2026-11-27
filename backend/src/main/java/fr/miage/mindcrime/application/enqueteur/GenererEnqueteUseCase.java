package fr.miage.mindcrime.application.enqueteur;

import fr.miage.mindcrime.application.port.EnqueteRepositoryPort;
import fr.miage.mindcrime.moteur.generation.GenerateurEnqueteBacktracking;
import fr.miage.mindcrime.moteur.generation.ParametresGeneration;
import fr.miage.mindcrime.moteur.generation.ResultatGeneration;
import fr.miage.mindcrime.moteur.modele.Enquete;
import fr.miage.mindcrime.moteur.modele.StatutUnicite;

public class GenererEnqueteUseCase {

    private final GenerateurEnqueteBacktracking generateur;
    private final EnqueteRepositoryPort repository;

    public GenererEnqueteUseCase(
            GenerateurEnqueteBacktracking generateur,
            EnqueteRepositoryPort repository
    ) {
        this.generateur = generateur;
        this.repository = repository;
    }

    public Enquete executer(String partieId, long seed, int nombreSuspects) {
        if (!repository.partieExiste(partieId)) {
            throw new PartieIntrouvableException(partieId);
        }
        ParametresGeneration parametres = new ParametresGeneration(partieId, seed, nombreSuspects);
        ResultatGeneration resultat = generateur.generer(parametres);
        if (resultat.statut() != StatutUnicite.COUPABLE_UNIQUE || resultat.enquete() == null) {
            throw new GenerationEnqueteException(resultat.statut());
        }
        return repository.sauvegarder(resultat.enquete());
    }
}