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
@Table(name = "enquete_suspect")
public class EnqueteSuspectEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enquete_id", nullable = false)
    private EnqueteEntity enquete;

    @Column(name = "suspect_code", nullable = false, length = 32)
    private String suspectCode;

    @Column(name = "nom", nullable = false, length = 128)
    private String nom;

    @Column(name = "profil", nullable = false, length = 128)
    private String profil;

    protected EnqueteSuspectEntity() {
    }

    public EnqueteSuspectEntity(EnqueteEntity enquete, String suspectCode, String nom, String profil) {
        this.enquete = enquete;
        this.suspectCode = suspectCode;
        this.nom = nom;
        this.profil = profil;
    }

    public String getSuspectCode() {
        return suspectCode;
    }

    public String getNom() {
        return nom;
    }

    public String getProfil() {
        return profil;
    }
}