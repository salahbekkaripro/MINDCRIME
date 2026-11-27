package fr.miage.mindcrime.application.enqueteur;

import fr.miage.mindcrime.application.port.EnqueteRepositoryPort;
import fr.miage.mindcrime.moteur.modele.Enquete;

public class ConsulterEnqueteUseCase {

    private final EnqueteRepositoryPort repository;

    public ConsulterEnqueteUseCase(EnqueteRepositoryPort repository) {
        this.repository = repository;
    }

    public Enquete executer(String partieId, String enqueteId) {
        if (!repository.partieExiste(partieId)) {
            throw new PartieIntrouvableException(partieId);
        }
        return repository.trouverParIdEtPartie(partieId, enqueteId)
                .orElseThrow(() -> new EnqueteIntrouvableException(enqueteId));
    }
}