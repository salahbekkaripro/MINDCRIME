package fr.miage.mindcrime.application.enqueteur;

public class EnqueteIntrouvableException extends RuntimeException {
    public EnqueteIntrouvableException(String enqueteId) {
        super("Enquete introuvable : " + enqueteId);
    }
}