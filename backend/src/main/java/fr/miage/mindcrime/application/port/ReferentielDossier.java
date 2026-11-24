package fr.miage.mindcrime.application.port;

import java.util.List;
import java.util.UUID;

public record ReferentielDossier(UUID partieId, String titre, String fuseau,
                                 List<ReferenceDossier> suspects, List<ReferenceDossier> lieux) {
    public ReferentielDossier {
        suspects = List.copyOf(suspects);
        lieux = List.copyOf(lieux);
    }
}
