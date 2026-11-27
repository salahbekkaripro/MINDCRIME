package fr.miage.mindcrime.application.enqueteur;

public class PartieIntrouvableException extends RuntimeException {
    public PartieIntrouvableException(String partieId) {
        super("Partie introuvable : " + partieId);
    }
}