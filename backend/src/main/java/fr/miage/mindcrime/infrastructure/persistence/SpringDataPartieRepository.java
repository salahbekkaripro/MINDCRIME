package fr.miage.mindcrime.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataPartieRepository extends JpaRepository<PartieEntity, String> {
}