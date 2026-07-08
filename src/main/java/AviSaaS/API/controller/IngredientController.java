package AviSaaS.API.controller;


import AviSaaS.API.dto.request.AchatIngredientRequest;
import AviSaaS.API.dto.request.CreateIngredientRequest;
import AviSaaS.API.dto.request.UpdateIngredientRequest;
import AviSaaS.API.dto.response.IngredientResponse;
import AviSaaS.API.security.JwtService;
import AviSaaS.API.service.AlimentationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/ingredients")
@RequiredArgsConstructor
public class IngredientController {

    private final AlimentationService alimentationService;
    private final JwtService jwtService;

    @PostMapping
    public ResponseEntity<IngredientResponse> creer(
            @Valid @RequestBody CreateIngredientRequest req,
            HttpServletRequest request) {
        return ResponseEntity.status(201)
                .body(alimentationService.creerIngredient(req, tenantId(request)));
    }

    @GetMapping
    public ResponseEntity<List<IngredientResponse>> lister(HttpServletRequest request) {
        return ResponseEntity.ok(alimentationService.listerIngredients(tenantId(request)));
    }

    @PostMapping("/{id}/achats")
    public ResponseEntity<IngredientResponse> achat(
            @PathVariable UUID id,
            @Valid @RequestBody AchatIngredientRequest req,
            HttpServletRequest request) {
        return ResponseEntity.status(201)
                .body(alimentationService.enregistrerAchat(id, tenantId(request), req));
    }

    private UUID tenantId(HttpServletRequest req) {
        return jwtService.extractTenantId(req.getHeader("Authorization").substring(7));
    }
//
    @PatchMapping("/{id}")
    public ResponseEntity<IngredientResponse> modifier(
            @PathVariable UUID id,
            @RequestBody UpdateIngredientRequest req,
            HttpServletRequest request) {
        return ResponseEntity.ok(
                alimentationService.modifierIngredient(id, tenantId(request), req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(
            @PathVariable UUID id, HttpServletRequest request) {
        alimentationService.supprimerIngredient(id, tenantId(request));
        return ResponseEntity.noContent().build();
    }
}