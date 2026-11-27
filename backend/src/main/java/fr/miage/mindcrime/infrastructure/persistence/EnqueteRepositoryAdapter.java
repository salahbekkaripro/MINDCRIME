package fr.miage.mindcrime.infrastructure.persistence;

import fr.miage.mindcrime.application.port.EnqueteRepositoryPort;
import fr.miage.mindcrime.moteur.modele.Creneau;
import fr.miage.mindcrime.moteur.modele.Crime;
import fr.miage.mindcrime.moteur.modele.Enquete;
import fr.miage.mindcrime.moteur.modele.Indice;
import fr.miage.mindcrime.moteur.modele.Lieu;
import fr.miage.mindcrime.moteur.modele.StatutUnicite;
import fr.miage.mindcrime.moteur.modele.Suspect;
import fr.miage.mindcrime.moteur.modele.TypeIndice;
import fr.miage.mindcrime.moteur.modele.VeriteTerrain;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class EnqueteRepositoryAdapter implements EnqueteRepositoryPort {

    private static final List<Lieu> LIEUX_DEFAUT = List.of(
            new Lieu("L1", "Salon"),
            new Lieu("L2", "Bibliotheque"),
            new Lieu("L3", "Cuisine"),
            new Lieu("L4", "Jardin"),
            new Lieu("L5", "Bureau"),
            new Lieu("L6", "Cave")
    );

    private static final List<Creneau> CRENEAUX_DEFAUT = List.of(
            new Creneau(0, "20h"),
            new Creneau(1, "21h"),
            new Creneau(2, "22h")
    );

    private final SpringDataPartieRepository partieRepository;
    private final SpringDataEnqueteRepository enqueteRepository;

    public EnqueteRepositoryAdapter(
            SpringDataPartieRepository partieRepository,
            SpringDataEnqueteRepository enqueteRepository
    ) {
        this.partieRepository = partieRepository;
        this.enqueteRepository = enqueteRepository;
    }

    @Override
    public boolean partieExiste(String partieId) {
        return partieRepository.existsById(partieId);
    }

    @Override
    @Transactional
    public String creerPartie(String partieId) {
        if (!partieRepository.existsById(partieId)) {
            partieRepository.save(new PartieEntity(partieId, LocalDateTime.now()));
        }
        return partieId;
    }

    @Override
    @Transactional
    public Enquete sauvegarder(Enquete enquete) {
        if (enqueteRepository.existsById(enquete.id())) {
            enqueteRepository.deleteById(enquete.id());
            enqueteRepository.flush();
        }

        EnqueteEntity entity = new EnqueteEntity(
                enquete.id(),
                enquete.partieId(),
                enquete.seed(),
                enquete.crime().lieuId(),
                enquete.crime().creneau().ordre(),
                enquete.crime().creneau().libelle(),
                enquete.veriteTerrain().coupableId(),
                enquete.statutUnicite().name()
        );

        for (Suspect s : enquete.suspects()) {
            entity.getSuspects().add(new EnqueteSuspectEntity(entity, s.id(), s.nom(), s.profil()));
        }

        for (Indice ind : enquete.indices()) {
            entity.getIndices().add(new EnqueteIndiceEntity(
                    entity,
                    ind.id(),
                    ind.type().name(),
                    ind.suspectId(),
                    ind.lieuId(),
                    ind.creneau().ordre(),
                    ind.creneau().libelle(),
                    ind.description(),
                    ind.visible()
            ));
        }

        enqueteRepository.save(entity);
        return enquete;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Enquete> trouverParIdEtPartie(String partieId, String enqueteId) {
        return enqueteRepository.findByIdAndPartieId(enqueteId, partieId).map(this::versDomaine);
    }

    private Enquete versDomaine(EnqueteEntity entity) {
        Crime crime = new Crime(
                entity.getLieuCrime(),
                new Creneau(entity.getCreneauCrimeOrdre(), entity.getCreneauCrimeLibelle())
        );

        List<Suspect> suspects = entity.getSuspects().stream()
                .map(s -> new Suspect(s.getSuspectCode(), s.getNom(), s.getProfil()))
                .toList();

        List<Indice> indices = entity.getIndices().stream()
                .map(i -> new Indice(
                        i.getIndiceCode(),
                        TypeIndice.valueOf(i.getTypeIndice()),
                        i.getSuspectId(),
                        i.getLieuId(),
                        new Creneau(i.getCreneauOrdre(), i.getCreneauLibelle()),
                        i.getDescription(),
                        i.isVisible()
                ))
                .toList();

        return new Enquete(
                entity.getId(),
                entity.getPartieId(),
                entity.getSeed(),
                crime,
                suspects,
                LIEUX_DEFAUT,
                CRENEAUX_DEFAUT,
                indices,
                new VeriteTerrain(entity.getCoupableId(), Map.of()),
                StatutUnicite.valueOf(entity.getStatutUnicite())
        );
    }
}