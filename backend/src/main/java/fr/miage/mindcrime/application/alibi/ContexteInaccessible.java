package fr.miage.mindcrime.application.alibi;

public final class ContexteInaccessible extends RuntimeException {
    public ContexteInaccessible() { super("Dossier ou référence inaccessible."); }
}
