package fr.miage.mindcrime.infrastructure.persistence;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "enquete")
public class EnqueteEntity {

    @Id
    @Column(name = "id", length = 128)
    private String id;

    @Column(name = "partie_id", nullable = false, length = 64)
    private String partieId;

    @Column(name = "seed", nullable = false)
    private long seed;

    @Column(name = "lieu_crime", nullable = false, length = 32)
    private String lieuCrime;

    @Column(name = "creneau_crime_ordre", nullable = false)
    private int creneauCrimeOrdre;

    @Column(name = "creneau_crime_libelle", nullable = false, length = 32)
    private String creneauCrimeLibelle;

    @Column(name = "coupable_id", nullable = false, length = 32)
    private String coupableId;

    @Column(name = "statut_unicite", nullable = false, length = 32)
    private String statutUnicite;

    @OneToMany(mappedBy = "enquete", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EnqueteSuspectEntity> suspects = new ArrayList<>();

    @OneToMany(mappedBy = "enquete", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EnqueteIndiceEntity> indices = new ArrayList<>();

    protected EnqueteEntity() {
    }

    public EnqueteEntity(
            String id,
            String partieId,
            long seed,
            String lieuCrime,
            int creneauCrimeOrdre,
            String creneauCrimeLibelle,
            String coupableId,
            String statutUnicite
    ) {
        this.id = id;
        this.partieId = partieId;
        this.seed = seed;
        this.lieuCrime = lieuCrime;
        this.creneauCrimeOrdre = creneauCrimeOrdre;
        this.creneauCrimeLibelle = creneauCrimeLibelle;
        this.coupableId = coupableId;
        this.statutUnicite = statutUnicite;
    }

    public String getId() {
        return id;
    }

    public String getPartieId() {
        return partieId;
    }

    public long getSeed() {
        return seed;
    }

    public String getLieuCrime() {
        return lieuCrime;
    }

    public int getCreneauCrimeOrdre() {
        return creneauCrimeOrdre;
    }

    public String getCreneauCrimeLibelle() {
        return creneauCrimeLibelle;
    }

    public String getCoupableId() {
        return coupableId;
    }

    public String getStatutUnicite() {
        return statutUnicite;
    }

    public List<EnqueteSuspectEntity> getSuspects() {
        return suspects;
    }

    public List<EnqueteIndiceEntity> getIndices() {
        return indices;
    }
}