package fr.miage.mindcrime.moteur.contraintes;

public final class LimiteAnalyseDepassee extends IllegalArgumentException {
    public LimiteAnalyseDepassee(int limite) {
        super("Limite de " + limite + " déclarations dépassée ; aucune analyse tronquée.");
    }
}
