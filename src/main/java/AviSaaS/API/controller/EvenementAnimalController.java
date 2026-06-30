package AviSaaS.API.controller;



import AviSaaS.API.dto.request.CreateEvenementAnimalRequest;
import AviSaaS.API.dto.response.EvenementAnimalResponse;
import AviSaaS.API.security.JwtService;
import AviSaaS.API.service.EvenementAnimalService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/animaux/{animalId}/evenements")
@RequiredArgsConstructor
public class EvenementAnimalController {

    private final EvenementAnimalService evenementService;
    private final JwtService             jwtService;

    @PostMapping
    public ResponseEntity<EvenementAnimalResponse> ajouter(
            @PathVariable UUID animalId,
            @Valid @RequestBody CreateEvenementAnimalRequest req,
            HttpServletRequest request) {
        return ResponseEntity.status(201)
                .body(evenementService.ajouter(animalId, tenantId(request), req));
    }

    @GetMapping
    public ResponseEntity<List<EvenementAnimalResponse>> lister(
            @PathVariable UUID animalId, HttpServletRequest request) {
        return ResponseEntity.ok(evenementService.lister(animalId, tenantId(request)));
    }

    private UUID tenantId(HttpServletRequest req) {
        return jwtService.extractTenantId(req.getHeader("Authorization").substring(7));
    }
}
