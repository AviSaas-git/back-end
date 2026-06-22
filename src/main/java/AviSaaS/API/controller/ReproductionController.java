package AviSaaS.API.controller;

import AviSaaS.API.dto.request.CreateReproductionRequest;
import AviSaaS.API.dto.request.EnregistrerPorteeRequest;
import AviSaaS.API.dto.response.PorteeResponse;
import AviSaaS.API.dto.response.ReproductionResponse;
import AviSaaS.API.security.JwtService;
import AviSaaS.API.service.ReproductionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reproductions")
@RequiredArgsConstructor
public class ReproductionController {

    private final ReproductionService reproductionService;
    private final JwtService jwtService;

    @PostMapping
    public ResponseEntity<ReproductionResponse> creer(
            @Valid @RequestBody CreateReproductionRequest req, HttpServletRequest request) {
        return ResponseEntity.status(201)
                .body(reproductionService.creer(req, tenantId(request)));
    }

    @GetMapping
    public ResponseEntity<List<ReproductionResponse>> lister(HttpServletRequest request) {
        return ResponseEntity.ok(reproductionService.lister(tenantId(request)));
    }

    @GetMapping("/calendrier")
    public ResponseEntity<List<ReproductionResponse>> calendrier(
            @RequestParam(defaultValue = "30") int joursAVenir, HttpServletRequest request) {
        return ResponseEntity.ok(reproductionService.getCalendrier(tenantId(request), joursAVenir));
    }

    @PatchMapping("/{id}/confirmer-gestation")
    public ResponseEntity<ReproductionResponse> confirmer(
            @PathVariable UUID id, HttpServletRequest request) {
        return ResponseEntity.ok(reproductionService.confirmerGestation(id, tenantId(request)));
    }

    @PatchMapping("/{id}/echec")
    public ResponseEntity<ReproductionResponse> echec(
            @PathVariable UUID id, HttpServletRequest request) {
        return ResponseEntity.ok(reproductionService.declarerEchec(id, tenantId(request)));
    }

    @PostMapping("/{id}/portee")
    public ResponseEntity<PorteeResponse> enregistrerPortee(
            @PathVariable UUID id, @Valid @RequestBody EnregistrerPorteeRequest req,
            HttpServletRequest request) {
        return ResponseEntity.status(201)
                .body(reproductionService.enregistrerPortee(id, tenantId(request), req));
    }

    private UUID tenantId(HttpServletRequest req) {
        return jwtService.extractTenantId(req.getHeader("Authorization").substring(7));
    }
}