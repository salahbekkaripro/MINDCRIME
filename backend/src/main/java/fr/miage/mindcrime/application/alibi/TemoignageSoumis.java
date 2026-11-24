package fr.miage.mindcrime.application.alibi;

import fr.miage.mindcrime.moteur.contraintes.Declaration;
import fr.miage.mindcrime.moteur.contraintes.ResultatAnalyse;

public record TemoignageSoumis(Declaration declaration, ResultatAnalyse analyse) { }
