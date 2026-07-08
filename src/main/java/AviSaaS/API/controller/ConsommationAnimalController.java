package AviSaaS.API.controller;


import AviSaaS.API.dto.request.CreateConsommationRequest;
import AviSaaS.API.dto.response.AlimentationBandeResponse;
import AviSaaS.API.dto.response.ConsommationResponse;
import AviSaaS.API.security.JwtService;
import AviSaaS.API.service.AlimentationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/animaux/{animalId}/alimentation")
@RequiredArgsConstructor
public class ConsommationAnimalController {

    private final AlimentationService alimentationService;
    private final JwtService jwtService;

    @PostMapping
    public ResponseEntity<ConsommationResponse> enregistrer(
            @PathVariable UUID animalId,
            @Valid @RequestBody CreateConsommationRequest req,
            HttpServletRequest request) {
        return ResponseEntity.status(201)
                .body(alimentationService.enregistrerConsommationAnimal(
                        animalId, tenantId(request), req));
    }

    @GetMapping
    public ResponseEntity<AlimentationBandeResponse> getAlimentation(
            @PathVariable UUID animalId, HttpServletRequest request) {
        return ResponseEntity.ok(
                alimentationService.getAlimentationAnimal(animalId, tenantId(request)));
    }

    private UUID tenantId(HttpServletRequest req) {
        return jwtService.extractTenantId(req.getHeader("Authorization").substring(7));
    }
}