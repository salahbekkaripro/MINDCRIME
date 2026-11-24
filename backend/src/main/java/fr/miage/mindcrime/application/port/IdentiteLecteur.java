package fr.miage.mindcrime.application.port;

import java.util.UUID;

/** Identité fournie par l'adaptateur de compte local provisoire. */
public record IdentiteLecteur(UUID acteurId, String nom, UUID dossierId) { }
