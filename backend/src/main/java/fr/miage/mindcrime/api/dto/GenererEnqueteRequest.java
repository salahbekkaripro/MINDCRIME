package fr.miage.mindcrime.api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record GenererEnqueteRequest(
        Long seed,
        @Min(value = 3, message = "nombreSuspects doit etre au moins 3")
        @Max(value = 6, message = "nombreSuspects doit etre au plus 6")
        Integer nombreSuspects
) {
    public long seedOuDefaut() {
        return seed != null ? seed : System.currentTimeMillis();
    }

    public int nombreSuspectsOuDefaut() {
        return nombreSuspects != null ? nombreSuspects : 5;
    }
}