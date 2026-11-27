package fr.miage.mindcrime.moteur.generation;

import fr.miage.mindcrime.moteur.modele.Creneau;
import fr.miage.mindcrime.moteur.modele.Crime;
import fr.miage.mindcrime.moteur.modele.Enquete;
import fr.miage.mindcrime.moteur.modele.Indice;
import fr.miage.mindcrime.moteur.modele.Lieu;
import fr.miage.mindcrime.moteur.modele.StatutUnicite;
import fr.miage.mindcrime.moteur.modele.Suspect;
import fr.miage.mindcrime.moteur.modele.TypeIndice;
import fr.miage.mindcrime.moteur.modele.VeriteTerrain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class GenerateurEnqueteBacktracking {

    private static final List<Suspect> CATALOGUE_SUSPECTS = List.of(
            new Suspect("S1", "Alice Moreau", "Majordome"),
            new Suspect("S2", "Bob Laurent", "Medecin"),
            new Suspect("S3", "Chloe Mercier", "Avocate"),
            new Suspect("S4", "David Garnier", "Collectionneur"),
            new Suspect("S5", "Emma Lefevre", "Journaliste"),
            new Suspect("S6", "Farid Benali", "Architecte")
    );

    private static final List<Lieu> CATALOGUE_LIEUX = List.of(
            new Lieu("L1", "Salon"),
            new Lieu("L2", "Bibliotheque"),
            new Lieu("L3", "Cuisine"),
            new Lieu("L4", "Jardin"),
            new Lieu("L5", "Bureau"),
            new Lieu("L6", "Cave")
    );

    private static final List<Creneau> CATALOGUE_CRENEAUX = List.of(
            new Creneau(0, "20h"),
            new Creneau(1, "21h"),
            new Creneau(2, "22h")
    );

    private final VerificateurUnicite verificateur;

    public GenerateurEnqueteBacktracking() {
        this(new VerificateurUnicite());
    }

    public GenerateurEnqueteBacktracking(VerificateurUnicite verificateur) {
        this.verificateur = verificateur;
    }

    public Map<String, Set<String>> grapheLieuxParDefaut() {
        Map<String, Set<String>> graphe = new LinkedHashMap<>();
        graphe.put("L1", Set.of("L2", "L4", "L5"));
        graphe.put("L2", Set.of("L1", "L3", "L5"));
        graphe.put("L3", Set.of("L2", "L4", "L6"));
        graphe.put("L4", Set.of("L1", "L3"));
        graphe.put("L5", Set.of("L1", "L2", "L6"));
        graphe.put("L6", Set.of("L3", "L5"));
        return Collections.unmodifiableMap(graphe);
    }

    public ResultatGeneration generer(ParametresGeneration parametres) {
        Random random = new Random(parametres.seed());
        List<Suspect> suspects = new ArrayList<>(CATALOGUE_SUSPECTS.subList(0, parametres.nombreSuspects()));
        List<Lieu> lieux = CATALOGUE_LIEUX;
        List<Creneau> creneaux = CATALOGUE_CRENEAUX;
        Map<String, Set<String>> graphe = grapheLieuxParDefaut();

        Suspect coupable = suspects.get(random.nextInt(suspects.size()));
        Lieu lieuCrime = lieux.get(random.nextInt(lieux.size()));
        Creneau creneauCrime = creneaux.get(random.nextInt(creneaux.size()));
        Crime crime = new Crime(lieuCrime.id(), creneauCrime);

        Map<String, Map<Creneau, String>> presences = new LinkedHashMap<>();
        for (Suspect s : suspects) {
            presences.put(s.id(), new LinkedHashMap<>());
        }

        int[] compteurNoeuds = new int[]{0};
        boolean veriteTrouvee = assignerBacktracking(
                0,
                suspects,
                creneaux,
                lieux,
                graphe,
                coupable.id(),
                crime,
                presences,
                random,
                compteurNoeuds,
                parametres.budgetNoeudsMax()
        );

        if (!veriteTrouvee) {
            StatutUnicite statut = compteurNoeuds[0] >= parametres.budgetNoeudsMax()
                    ? StatutUnicite.RECHERCHE_INCOMPLETE
                    : StatutUnicite.AUCUNE_SOLUTION;
            return new ResultatGeneration(statut, null, compteurNoeuds[0]);
        }

        VeriteTerrain verite = new VeriteTerrain(coupable.id(), presences);
        List<Indice> poolIndices = construirePoolIndices(suspects, lieux, creneaux, crime, verite, random);
        List<Indice> indicesSelectionnes = selectionnerIndicesGlouton(
                suspects,
                crime,
                poolIndices,
                graphe,
                coupable.id()
        );

        List<Indice> visibles = indicesSelectionnes.stream().filter(Indice::visible).toList();
        StatutUnicite statut = verificateur.verifier(suspects, crime, visibles, graphe, coupable.id());

        String enqueteId = "ENQ-" + parametres.partieId() + "-" + parametres.seed();
        Enquete enquete = new Enquete(
                enqueteId,
                parametres.partieId(),
                parametres.seed(),
                crime,
                suspects,
                lieux,
                creneaux,
                indicesSelectionnes,
                verite,
                statut
        );

        return new ResultatGeneration(statut, enquete, compteurNoeuds[0]);
    }

    private boolean assignerBacktracking(
            int indexCellule,
            List<Suspect> suspects,
            List<Creneau> creneaux,
            List<Lieu> lieux,
            Map<String, Set<String>> graphe,
            String coupableId,
            Crime crime,
            Map<String, Map<Creneau, String>> presences,
            Random random,
            int[] compteurNoeuds,
            int budgetNoeudsMax
    ) {
        int totalCellules = suspects.size() * creneaux.size();
        if (indexCellule == totalCellules) {
            return true;
        }
        if (compteurNoeuds[0] >= budgetNoeudsMax) {
            return false;
        }
        compteurNoeuds[0]++;

        Suspect suspect = suspects.get(indexCellule / creneaux.size());
        Creneau creneau = creneaux.get(indexCellule % creneaux.size());

        List<String> candidatsLieux = new ArrayList<>();
        for (Lieu lieu : lieux) {
            candidatsLieux.add(lieu.id());
        }
        Collections.shuffle(candidatsLieux, random);

        for (String lieuId : candidatsLieux) {
            if (!estPlacementValide(suspect.id(), creneau, lieuId, coupableId, crime, presences, creneaux, graphe)) {
                continue;
            }
            presences.get(suspect.id()).put(creneau, lieuId);
            if (assignerBacktracking(
                    indexCellule + 1,
                    suspects,
                    creneaux,
                    lieux,
                    graphe,
                    coupableId,
                    crime,
                    presences,
                    random,
                    compteurNoeuds,
                    budgetNoeudsMax
            )) {
                return true;
            }
            presences.get(suspect.id()).remove(creneau);
        }
        return false;
    }

    private boolean estPlacementValide(
            String suspectId,
            Creneau creneau,
            String lieuId,
            String coupableId,
            Crime crime,
            Map<String, Map<Creneau, String>> presences,
            List<Creneau> creneaux,
            Map<String, Set<String>> graphe
    ) {
        if (creneau.equals(crime.creneau())) {
            if (suspectId.equals(coupableId) && !lieuId.equals(crime.lieuId())) {
                return false;
            }
            if (!suspectId.equals(coupableId) && lieuId.equals(crime.lieuId())) {
                return false;
            }
        }
        if (creneau.ordre() > 0) {
            Creneau precedent = creneaux.get(creneau.ordre() - 1);
            String lieuPrecedent = presences.get(suspectId).get(precedent);
            if (lieuPrecedent != null
                    && !lieuPrecedent.equals(lieuId)
                    && !graphe.getOrDefault(lieuPrecedent, Set.of()).contains(lieuId)) {
                return false;
            }
        }
        return true;
    }

    private List<Indice> construirePoolIndices(
            List<Suspect> suspects,
            List<Lieu> lieux,
            List<Creneau> creneaux,
            Crime crime,
            VeriteTerrain verite,
            Random random
    ) {
        Map<String, String> nomsLieux = new HashMap<>();
        for (Lieu l : lieux) {
            nomsLieux.put(l.id(), l.nom());
        }

        List<Indice> pool = new ArrayList<>();
        int idx = 1;

        for (Suspect suspect : suspects) {
            for (Creneau creneau : creneaux) {
                String lieuId = verite.lieuDe(suspect.id(), creneau);
                String nomLieu = nomsLieux.getOrDefault(lieuId, lieuId);

                if (!suspect.id().equals(verite.coupableId()) && creneau.equals(crime.creneau())) {
                    pool.add(new Indice(
                            "IND-" + (idx++),
                            TypeIndice.ALIBI_CONFIRME,
                            suspect.id(),
                            lieuId,
                            creneau,
                            suspect.nom() + " a un alibi confirme dans " + nomLieu + " a " + creneau.libelle(),
                            false
                    ));
                    pool.add(new Indice(
                            "IND-" + (idx++),
                            TypeIndice.ABSENCE_LIEU,
                            suspect.id(),
                            crime.lieuId(),
                            creneau,
                            suspect.nom() + " n'etait pas dans " + nomsLieux.get(crime.lieuId()) + " a " + creneau.libelle(),
                            false
                    ));
                } else if (suspect.id().equals(verite.coupableId()) && !creneau.equals(crime.creneau())) {
                    pool.add(new Indice(
                            "IND-" + (idx++),
                            TypeIndice.INDICE_MATERIEL,
                            suspect.id(),
                            lieuId,
                            creneau,
                            "Une trace confirme le passage de " + suspect.nom() + " dans " + nomLieu + " a " + creneau.libelle(),
                            false
                    ));
                } else if (!suspect.id().equals(verite.coupableId())) {
                    pool.add(new Indice(
                            "IND-" + (idx++),
                            TypeIndice.PRESENCE_LIEU,
                            suspect.id(),
                            lieuId,
                            creneau,
                            suspect.nom() + " a ete apercu dans " + nomLieu + " a " + creneau.libelle(),
                            false
                    ));
                }
            }
        }

        Collections.shuffle(pool, random);
        return pool;
    }

    private List<Indice> selectionnerIndicesGlouton(
            List<Suspect> suspects,
            Crime crime,
            List<Indice> pool,
            Map<String, Set<String>> graphe,
            String coupableId
    ) {
        Set<String> compatibles = new LinkedHashSet<>();
        for (Suspect s : suspects) {
            compatibles.add(s.id());
        }

        List<Indice> resultat = new ArrayList<>(pool);
        Set<Integer> indicesVisiblesIdx = new LinkedHashSet<>();

        while (compatibles.size() > 1) {
            int meilleurIdx = -1;
            int meilleurGain = 0;

            for (int i = 0; i < resultat.size(); i++) {
                if (indicesVisiblesIdx.contains(i)) {
                    continue;
                }
                Indice candidat = resultat.get(i);
                if (candidat.suspectId().equals(coupableId)
                        && verificateur.elimineSuspect(candidat, crime, graphe)) {
                    continue;
                }
                if (compatibles.contains(candidat.suspectId())
                        && !candidat.suspectId().equals(coupableId)
                        && verificateur.elimineSuspect(candidat, crime, graphe)) {
                    meilleurIdx = i;
                    meilleurGain = 1;
                    break;
                }
            }

            if (meilleurIdx == -1 || meilleurGain == 0) {
                break;
            }

            indicesVisiblesIdx.add(meilleurIdx);
            compatibles.remove(resultat.get(meilleurIdx).suspectId());
        }

        for (int i = 0; i < resultat.size(); i++) {
            Indice ind = resultat.get(i);
            if (!indicesVisiblesIdx.contains(i) && ind.type() == TypeIndice.INDICE_MATERIEL) {
                indicesVisiblesIdx.add(i);
                break;
            }
        }

        List<Indice> finalList = new ArrayList<>();
        for (int i = 0; i < resultat.size(); i++) {
            finalList.add(resultat.get(i).avecVisibilite(indicesVisiblesIdx.contains(i)));
        }
        return finalList;
    }
}