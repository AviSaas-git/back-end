package AviSaaS.API.controller;

import AviSaaS.API.dto.request.CreateMortaliteRequest;
import AviSaaS.API.dto.response.MortaliteResponse;
import AviSaaS.API.security.JwtService;
import AviSaaS.API.service.MortaliteService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/bandes/{bandeId}/mortalites")
@RequiredArgsConstructor
public class MortaliteController {

    private final MortaliteService mortaliteService;
    private final JwtService jwtService;

    @PostMapping
    public ResponseEntity<MortaliteResponse> enregistrer(
            @PathVariable UUID bandeId,
            @Valid @RequestBody CreateMortaliteRequest req,
            HttpServletRequest request) {
        return ResponseEntity.status(201)
                .body(mortaliteService.enregistrer(bandeId, tenantId(request), req));
    }

    @GetMapping
    public ResponseEntity<List<MortaliteResponse>> lister(
            @PathVariable UUID bandeId, HttpServletRequest request) {
        return ResponseEntity.ok(mortaliteService.lister(bandeId, tenantId(request)));
    }

    private UUID tenantId(HttpServletRequest req) {
        return jwtService.extractTenantId(req.getHeader("Authorization").substring(7));
    }

    @DeleteMapping("/{mortaliteId}")
    public ResponseEntity<Void> supprimer(
            @PathVariable UUID bandeId,
            @PathVariable UUID mortaliteId,
            HttpServletRequest request) {
        mortaliteService.supprimer(mortaliteId, tenantId(request));
        return ResponseEntity.noContent().build();
    }
}