package fr.miage.mindcrime.api.rest;

import fr.miage.mindcrime.application.alibi.ContexteInaccessible;
import fr.miage.mindcrime.moteur.contraintes.ErreurContratAnalyse;
import fr.miage.mindcrime.moteur.contraintes.LimiteAnalyseDepassee;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class ErreursApi {
    @ExceptionHandler(ContexteInaccessible.class) ProblemDetail inaccessible() {
        return probleme(HttpStatus.NOT_FOUND, "Dossier ou référence inaccessible.");
    }
    @ExceptionHandler({MethodArgumentNotValidException.class, ErreurContratAnalyse.class})
    ProblemDetail invalide() { return probleme(HttpStatus.UNPROCESSABLE_CONTENT, "Les informations du témoignage sont invalides ou incomplètes."); }
    @ExceptionHandler(LimiteAnalyseDepassee.class) ProblemDetail limite() {
        return probleme(HttpStatus.UNPROCESSABLE_CONTENT, "La limite de déclarations analysables est atteinte. Aucune analyse tronquée n'a été effectuée.");
    }
    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    ProblemDetail illisible() { return probleme(HttpStatus.BAD_REQUEST, "La requête ne respecte pas le format attendu."); }
    @ExceptionHandler(DataAccessException.class) ProblemDetail stockage() {
        return probleme(HttpStatus.SERVICE_UNAVAILABLE, "Le stockage est temporairement indisponible. Réessayez plus tard.");
    }
    private ProblemDetail probleme(HttpStatus statut, String detail) {
        return ProblemDetail.forStatusAndDetail(statut, detail);
    }
}
