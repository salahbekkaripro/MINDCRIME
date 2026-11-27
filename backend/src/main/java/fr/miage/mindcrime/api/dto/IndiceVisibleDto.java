package fr.miage.mindcrime.api.dto;

public record IndiceVisibleDto(
        String id,
        String type,
        String suspectId,
        String lieuId,
        String creneauLibelle,
        String description
) {
}