package fr.miage.mindcrime.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataEnqueteRepository extends JpaRepository<EnqueteEntity, String> {
    Optional<EnqueteEntity> findByIdAndPartieId(String id, String partieId);
}