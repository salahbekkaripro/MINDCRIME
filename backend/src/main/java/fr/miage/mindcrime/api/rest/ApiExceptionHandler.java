package fr.miage.mindcrime.api.rest;

import fr.miage.mindcrime.application.enqueteur.EnqueteIntrouvableException;
import fr.miage.mindcrime.application.enqueteur.GenerationEnqueteException;
import fr.miage.mindcrime.application.enqueteur.PartieIntrouvableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidation(MethodArgumentNotValidException ex) {
        String detail = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .findFirst()
                .orElse("Parametres de requete invalides");
        return problem(HttpStatus.BAD_REQUEST, "Parametres invalides", detail, "validation-erreur");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleIllegalArgument(IllegalArgumentException ex) {
        return problem(HttpStatus.BAD_REQUEST, "Parametres invalides", ex.getMessage(), "parametre-invalide");
    }

    @ExceptionHandler({PartieIntrouvableException.class, EnqueteIntrouvableException.class})
    public ResponseEntity<ProblemDetail> handleNotFound(RuntimeException ex) {
        return problem(HttpStatus.NOT_FOUND, "Ressource introuvable", ex.getMessage(), "ressource-introuvable");
    }

    @ExceptionHandler(GenerationEnqueteException.class)
    public ResponseEntity<ProblemDetail> handleGeneration(GenerationEnqueteException ex) {
        return problem(HttpStatus.UNPROCESSABLE_ENTITY, "Generation impossible", ex.getMessage(), "generation-impossible");
    }

    private ResponseEntity<ProblemDetail> problem(HttpStatus status, String title, String detail, String code) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(title);
        problem.setType(URI.create("https://mindcrime.miage.fr/erreurs/" + code));
        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem);
    }
}