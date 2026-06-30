package AviSaaS.API.controller;

import AviSaaS.API.dto.request.*;
import AviSaaS.API.dto.response.*;
import AviSaaS.API.security.JwtService;
import AviSaaS.API.service.PonteService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class PonteController {

    private final PonteService ponteService;
    private final JwtService   jwtService;

    // ── Collectes ────────────────────────────────────────────────────
    @PostMapping("/api/v1/bandes/{bandeId}/collectes")
    public ResponseEntity<CollecteResponse> enregistrerCollecte(
            @PathVariable UUID bandeId,
            @Valid @RequestBody CreateCollecteRequest req,
            HttpServletRequest request) {
        return ResponseEntity.status(201)
                .body(ponteService.enregistrerCollecte(bandeId, tenantId(request), req));
    }

    @GetMapping("/api/v1/bandes/{bandeId}/collectes")
    public ResponseEntity<List<CollecteResponse>> listerCollectes(
            @PathVariable UUID bandeId, HttpServletRequest request) {
        return ResponseEntity.ok(ponteService.listerCollectes(bandeId, tenantId(request)));
    }

    // ── Ventes ───────────────────────────────────────────────────────
    @PostMapping("/api/v1/bandes/{bandeId}/ventes-oeufs")
    public ResponseEntity<VenteResponse> enregistrerVente(
            @PathVariable UUID bandeId,
            @Valid @RequestBody CreateVenteRequest req,
            HttpServletRequest request) {
        return ResponseEntity.status(201)
                .body(ponteService.enregistrerVente(bandeId, tenantId(request), req));
    }

    @GetMapping("/api/v1/bandes/{bandeId}/ventes-oeufs")
    public ResponseEntity<List<VenteResponse>> listerVentes(
            @PathVariable UUID bandeId, HttpServletRequest request) {
        return ResponseEntity.ok(ponteService.listerVentes(bandeId, tenantId(request)));
    }

    // ── Stock ────────────────────────────────────────────────────────
    @GetMapping("/api/v1/bandes/{bandeId}/stock-oeufs")
    public ResponseEntity<StockOeufsResponse> getStock(
            @PathVariable UUID bandeId, HttpServletRequest request) {
        return ResponseEntity.ok(ponteService.getStock(bandeId, tenantId(request)));
    }

    // ── Dashboard ponte ──────────────────────────────────────────────
    @GetMapping("/api/v1/bandes/{bandeId}/ponte-dashboard")
    public ResponseEntity<PonteDashboardResponse> getDashboard(
            @PathVariable UUID bandeId, HttpServletRequest request) {
        return ResponseEntity.ok(ponteService.getDashboard(bandeId, tenantId(request)));
    }

    // ── Prix de référence ────────────────────────────────────────────
    @PutMapping("/api/v1/ponte/prix-reference")
    public ResponseEntity<Void> updatePrix(
            @RequestBody UpdatePrixReferenceRequest req,
            HttpServletRequest request) {
        ponteService.updatePrixReference(tenantId(request), req);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/v1/ponte/prix-reference")
    public ResponseEntity<Map<String, Double>> getPrix(HttpServletRequest request) {
        return ResponseEntity.ok(ponteService.getPrixReference(tenantId(request)));
    }

    private UUID tenantId(HttpServletRequest req) {
        return jwtService.extractTenantId(req.getHeader("Authorization").substring(7));
    }
}