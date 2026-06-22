package AviSaaS.API.controller;

import AviSaaS.API.dto.request.CreateAnimalRequest;
import AviSaaS.API.dto.request.UpdateSexeRequest;
import AviSaaS.API.dto.response.AnimalResponse;
import AviSaaS.API.security.JwtService;
import AviSaaS.API.service.AnimalService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("/api/v1/animaux")
@RequiredArgsConstructor
public class AnimalController {

    private final AnimalService animalService;
    private final JwtService    jwtService;

    @PostMapping
    public ResponseEntity<AnimalResponse> creer(
            @Valid @RequestBody CreateAnimalRequest req,
            HttpServletRequest request) {
        var tenantId = extractTenantId(request);
        return ResponseEntity.status(201)
                .body(animalService.creer(req, tenantId));
    }

    @GetMapping
    public ResponseEntity<List<AnimalResponse>> lister(
            HttpServletRequest request) {
        var tenantId = extractTenantId(request);
        return ResponseEntity.ok(animalService.lister(tenantId));
    }

    @GetMapping("/reproducteurs")
    public ResponseEntity<List<AnimalResponse>> getReproducteurs(
            @RequestParam UUID especeId,
            @RequestParam String sexe,
            HttpServletRequest request) {
        var tenantId = extractTenantId(request);
        return ResponseEntity.ok(
                animalService.getReproducteurs(tenantId, especeId, sexe));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnimalResponse> getById(
            @PathVariable UUID id, HttpServletRequest request) {
        return ResponseEntity.ok(
                animalService.getById(id, extractTenantId(request)));
    }

    @PatchMapping("/{id}/sexe")
    public ResponseEntity<AnimalResponse> updateSexe(
            @PathVariable UUID id, @Valid @RequestBody UpdateSexeRequest req,
            HttpServletRequest request) {
        return ResponseEntity.ok(
                animalService.mettreAJourSexe(id, extractTenantId(request), req.getSexe()));
    }

    private java.util.UUID extractTenantId(HttpServletRequest req) {
        return jwtService.extractTenantId(
                req.getHeader("Authorization").substring(7));
    }
}