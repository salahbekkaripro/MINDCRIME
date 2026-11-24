package fr.miage.mindcrime.application.port;

import java.util.UUID;

/** Référence de lecture provisoire, à remplacer par les ports communs D1/D4. */
public record ReferenceDossier(UUID id, String nom) { }
