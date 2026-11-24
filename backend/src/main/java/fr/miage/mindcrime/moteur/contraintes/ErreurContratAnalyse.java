package fr.miage.mindcrime.moteur.contraintes;

/** Entrée incohérente : ne représente jamais une contradiction du jeu. */
public final class ErreurContratAnalyse extends IllegalArgumentException {
    public ErreurContratAnalyse(String message) { super(message); }
}
