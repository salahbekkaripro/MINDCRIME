package fr.miage.mindcrime.api.rest;

import fr.miage.mindcrime.api.dto.CreerPartieResponse;
import fr.miage.mindcrime.api.dto.EnqueteVisibleDto;
import fr.miage.mindcrime.api.dto.GenererEnqueteRequest;
import fr.miage.mindcrime.application.enqueteur.ConsulterEnqueteUseCase;
import fr.miage.mindcrime.application.enqueteur.GenererEnqueteUseCase;
import fr.miage.mindcrime.application.port.EnqueteRepositoryPort;
import fr.miage.mindcrime.moteur.modele.Enquete;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class EnqueteController {

    private final EnqueteRepositoryPort repository;
    private final GenererEnqueteUseCase genererEnqueteUseCase;
    private final ConsulterEnqueteUseCase consulterEnqueteUseCase;

    public EnqueteController(
            EnqueteRepositoryPort repository,
            GenererEnqueteUseCase genererEnqueteUseCase,
            ConsulterEnqueteUseCase consulterEnqueteUseCase
    ) {
        this.repository = repository;
        this.genererEnqueteUseCase = genererEnqueteUseCase;
        this.consulterEnqueteUseCase = consulterEnqueteUseCase;
    }

    @PostMapping("/parties")
    public ResponseEntity<CreerPartieResponse> creerPartie() {
        String partieId = "PARTIE-" + UUID.randomUUID().toString().substring(0, 8);
        repository.creerPartie(partieId);
        return ResponseEntity.status(HttpStatus.CREATED).body(new CreerPartieResponse(partieId));
    }

    @PostMapping("/parties/{partieId}/enquetes")
    public ResponseEntity<EnqueteVisibleDto> genererEnquete(
            @PathVariable String partieId,
            @Valid @RequestBody(required = false) GenererEnqueteRequest request
    ) {
        GenererEnqueteRequest req = request != null ? request : new GenererEnqueteRequest(null, null);
        Enquete enquete = genererEnqueteUseCase.executer(
                partieId,
                req.seedOuDefaut(),
                req.nombreSuspectsOuDefaut()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(EnqueteVisibleDto.depuisDomaine(enquete));
    }

    @GetMapping("/parties/{partieId}/enquetes/{enqueteId}")
    public ResponseEntity<EnqueteVisibleDto> consulterEnquete(
            @PathVariable String partieId,
            @PathVariable String enqueteId
    ) {
        Enquete enquete = consulterEnqueteUseCase.executer(partieId, enqueteId);
        return ResponseEntity.ok(EnqueteVisibleDto.depuisDomaine(enquete));
    }
}