package fr.miage.mindcrime.api.dto;

import fr.miage.mindcrime.application.alibi.CommandeTemoignage;
import fr.miage.mindcrime.moteur.contraintes.NatureDeclaration;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

public record NouveauTemoignage(@NotNull UUID suspectConcerneId, @NotNull UUID lieuId,
                                @NotNull Instant instant, @NotNull NatureDeclaration nature) {
    public CommandeTemoignage commande() { return new CommandeTemoignage(suspectConcerneId, lieuId, instant, nature); }
}
