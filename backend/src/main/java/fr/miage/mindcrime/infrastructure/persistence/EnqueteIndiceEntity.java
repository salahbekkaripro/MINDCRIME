package fr.miage.mindcrime.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "enquete_indice")
public class EnqueteIndiceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enquete_id", nullable = false)
    private EnqueteEntity enquete;

    @Column(name = "indice_code", nullable = false, length = 32)
    private String indiceCode;

    @Column(name = "type_indice", nullable = false, length = 32)
    private String typeIndice;

    @Column(name = "suspect_id", nullable = false, length = 32)
    private String suspectId;

    @Column(name = "lieu_id", nullable = false, length = 32)
    private String lieuId;

    @Column(name = "creneau_ordre", nullable = false)
    private int creneauOrdre;

    @Column(name = "creneau_libelle", nullable = false, length = 32)
    private String creneauLibelle;

    @Column(name = "description", nullable = false, length = 512)
    private String description;

    @Column(name = "visible", nullable = false)
    private boolean visible;

    protected EnqueteIndiceEntity() {
    }

    public EnqueteIndiceEntity(
            EnqueteEntity enquete,
            String indiceCode,
            String typeIndice,
            String suspectId,
            String lieuId,
            int creneauOrdre,
            String creneauLibelle,
            String description,
            boolean visible
    ) {
        this.enquete = enquete;
        this.indiceCode = indiceCode;
        this.typeIndice = typeIndice;
        this.suspectId = suspectId;
        this.lieuId = lieuId;
        this.creneauOrdre = creneauOrdre;
        this.creneauLibelle = creneauLibelle;
        this.description = description;
        this.visible = visible;
    }

    public String getIndiceCode() {
        return indiceCode;
    }

    public String getTypeIndice() {
        return typeIndice;
    }

    public String getSuspectId() {
        return suspectId;
    }

    public String getLieuId() {
        return lieuId;
    }

    public int getCreneauOrdre() {
        return creneauOrdre;
    }

    public String getCreneauLibelle() {
        return creneauLibelle;
    }

    public String getDescription() {
        return description;
    }

    public boolean isVisible() {
        return visible;
    }
}