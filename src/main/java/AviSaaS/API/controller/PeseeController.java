package AviSaaS.API.controller;

import AviSaaS.API.dto.request.CreatePeseeRequest;
import AviSaaS.API.dto.response.CourbePointResponse;
import AviSaaS.API.dto.response.PeseeResponse;
import AviSaaS.API.security.JwtService;
import AviSaaS.API.service.CourbeCroissanceService;
import AviSaaS.API.service.PeseeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/bandes/{bandeId}")
@RequiredArgsConstructor
public class PeseeController {

    private final PeseeService            peseeService;
    private final CourbeCroissanceService courbeCroissanceService;
    private final JwtService              jwtService;

    @PostMapping("/pesees")
    public ResponseEntity<PeseeResponse> enregistrer(
            @PathVariable UUID bandeId,
            @Valid @RequestBody CreatePeseeRequest req,
            HttpServletRequest request) {
        return ResponseEntity.status(201)
                .body(peseeService.enregistrer(bandeId, tenantId(request), req));
    }

    @GetMapping("/pesees")
    public ResponseEntity<List<PeseeResponse>> lister(
            @PathVariable UUID bandeId, HttpServletRequest request) {
        return ResponseEntity.ok(peseeService.lister(bandeId, tenantId(request)));
    }

    @GetMapping("/courbe-croissance")
    public ResponseEntity<List<CourbePointResponse>> courbe(
            @PathVariable UUID bandeId, HttpServletRequest request) {
        return ResponseEntity.ok(
                courbeCroissanceService.getCourbe(bandeId, tenantId(request)));
    }

    private UUID tenantId(HttpServletRequest req) {
        return jwtService.extractTenantId(req.getHeader("Authorization").substring(7));
    }
}