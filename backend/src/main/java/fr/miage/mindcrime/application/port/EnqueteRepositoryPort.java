package fr.miage.mindcrime.application.port;

import fr.miage.mindcrime.moteur.modele.Enquete;

import java.util.Optional;

public interface EnqueteRepositoryPort {
    boolean partieExiste(String partieId);
    String creerPartie(String partieId);
    Enquete sauvegarder(Enquete enquete);
    Optional<Enquete> trouverParIdEtPartie(String partieId, String enqueteId);
}