package fr.miage.mindcrime.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "partie")
public class PartieEntity {

    @Id
    @Column(name = "id", length = 64)
    private String id;

    @Column(name = "cree_le", nullable = false)
    private LocalDateTime creeLe;

    protected PartieEntity() {
    }

    public PartieEntity(String id, LocalDateTime creeLe) {
        this.id = id;
        this.creeLe = creeLe;
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getCreeLe() {
        return creeLe;
    }
}