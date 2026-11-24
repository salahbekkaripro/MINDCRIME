package fr.miage.mindcrime.moteur.contraintes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/** Sans état partagé : indexation attendue O(T), extraction O(K), mémoire O(T + K). */
public final class AnalyseurContraintesUnaires {
    public static final int LIMITE_PAR_DEFAUT = 200;
    private final int limite;

    public AnalyseurContraintesUnaires() { this(LIMITE_PAR_DEFAUT); }

    public AnalyseurContraintesUnaires(int limite) {
        if (limite < 1) throw new IllegalArgumentException("La limite doit être positive.");
        this.limite = limite;
    }

    public ResultatAnalyse analyser(InstantaneDeclarations instantane) {
        Objects.requireNonNull(instantane, "instantane");
        if ((long) instantane.declarations().size() + instantane.nonExploitables().size() > limite) {
            throw new LimiteAnalyseDepassee(limite);
        }
        Map<UUID, Object> identites = new HashMap<>();
        Map<CleFait, Set<UUID>> sources = new HashMap<>();
        Map<CleVariable, DomainePresence> domaines = new HashMap<>();
        Map<UUID, DeclarationNonExploitable> nonExploitables = new HashMap<>();

        for (var declaration : instantane.declarations()) {
            if (!declaration.partieId().equals(instantane.partieId())) {
                throw new ErreurContratAnalyse("L'instantané contient une autre partie.");
            }
            verifierIdentite(identites, declaration.id(), declaration);
            var cle = declaration.cleFait();
            var references = sources.get(cle);
            if (references == null) {
                references = new HashSet<>();
                sources.put(cle, references);
                domaines.computeIfAbsent(cle.variable(), ignore -> new DomainePresence()).propager(cle);
            }
            references.add(declaration.id());
        }
        for (var declaration : instantane.nonExploitables()) {
            verifierIdentite(identites, declaration.id(), declaration);
            nonExploitables.put(declaration.id(), declaration);
        }

        Set<Conflit> conflits = new HashSet<>();
        domaines.values().forEach(domaine -> domaine.ajouterConflitsDans(conflits));
        Map<CleFait, FaitNormalise> faits = new HashMap<>();
        sources.forEach((cle, references) -> faits.put(cle, new FaitNormalise(cle, references)));
        var completude = nonExploitables.isEmpty() ? CompletudeAnalyse.COMPLETE : CompletudeAnalyse.INCOMPLETE;
        var verdict = !conflits.isEmpty() ? VerdictAnalyse.CONTRADICTION
                : faits.isEmpty() && !nonExploitables.isEmpty() ? VerdictAnalyse.NON_VERIFIABLE
                : VerdictAnalyse.AUCUNE_CONTRADICTION_DETECTEE;
        return new ResultatAnalyse(instantane.partieId(), instantane.revision(), instantane.perimetre(),
                verdict, completude, faits, conflits, new ArrayList<>(nonExploitables.values()));
    }

    private void verifierIdentite(Map<UUID, Object> identites, UUID id, Object declaration) {
        var ancienne = identites.putIfAbsent(id, declaration);
        if (ancienne != null && !ancienne.equals(declaration)) {
            throw new ErreurContratAnalyse("Un identifiant désigne plusieurs déclarations différentes.");
        }
    }
}
