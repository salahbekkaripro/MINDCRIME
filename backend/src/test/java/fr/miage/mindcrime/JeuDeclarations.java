package fr.miage.mindcrime;

import fr.miage.mindcrime.application.port.PortDeclarations;
import fr.miage.mindcrime.moteur.contraintes.*;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

final class JeuDeclarations {
    static final UUID PARTIE = id(1), LECTEUR = id(2), AUTEUR = id(3), SUSPECT = id(4);
    static final UUID CUISINE = id(5), JARDIN = id(6), SALON = id(7);
    static final Instant HEURE = Instant.parse("2026-11-24T20:00:00Z");

    private JeuDeclarations() { }
    static UUID id(long valeur) { return new UUID(0, valeur); }
    static Declaration presence(long id, UUID lieu) { return declaration(id, lieu, NatureDeclaration.PRESENCE); }
    static Declaration absence(long id, UUID lieu) { return declaration(id, lieu, NatureDeclaration.ABSENCE); }
    static Declaration declaration(long id, UUID lieu, NatureDeclaration nature) {
        return new Declaration(id(id), PARTIE, AUTEUR, SUSPECT, lieu, HEURE, nature);
    }
    static DeclarationNonExploitable incomplete(long id) {
        return new DeclarationNonExploitable(id(id), Set.of("instant manquant"));
    }
    static InstantaneDeclarations instantane(List<Declaration> declarations) {
        return instantane(declarations, List.of());
    }
    static InstantaneDeclarations instantane(List<Declaration> declarations, List<DeclarationNonExploitable> incompletes) {
        return new InstantaneDeclarations(PARTIE, "visible-1", PortDeclarations.perimetreLecteur(LECTEUR),
                declarations, incompletes);
    }
}
