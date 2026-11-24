package fr.miage.mindcrime.moteur.contraintes;

import java.util.Objects;
import java.util.Set;

/** Deux clés, sans recopier leurs sources ni imposer d'ordre entre elles. */
public record Conflit(MotifConflit motif, Set<CleFait> faits) {
    public Conflit {
        Objects.requireNonNull(motif, "motif");
        faits = Set.copyOf(faits);
        if (faits.size() != 2) {
            throw new ErreurContratAnalyse("Un conflit direct relie deux faits distincts.");
        }
        var it = faits.iterator();
        var a = it.next();
        var b = it.next();
        boolean memeVariable = a.variable().equals(b.variable());
        boolean lieuxDistincts = a.nature() == NatureDeclaration.PRESENCE
                && b.nature() == NatureDeclaration.PRESENCE && !a.lieuId().equals(b.lieuId());
        boolean presenceAbsence = a.nature() != b.nature() && a.lieuId().equals(b.lieuId());
        if (!memeVariable || !(motif == MotifConflit.LIEUX_DISTINCTS ? lieuxDistincts : presenceAbsence)) {
            throw new ErreurContratAnalyse("Les faits ne justifient pas le motif du conflit.");
        }
    }
}
