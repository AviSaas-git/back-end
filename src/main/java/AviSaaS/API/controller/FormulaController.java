package AviSaaS.API.controller;

import AviSaaS.API.dto.request.CreateFormulaRequest;
import AviSaaS.API.dto.response.FormulaResponse;
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
@RequestMapping("/api/v1/formulas")
@RequiredArgsConstructor
public class FormulaController {

    private final AlimentationService alimentationService;
    private final JwtService jwtService;

    @PostMapping
    public ResponseEntity<FormulaResponse> creer(
            @Valid @RequestBody CreateFormulaRequest req,
            HttpServletRequest request) {
        return ResponseEntity.status(201)
                .body(alimentationService.creerFormule(req, tenantId(request)));
    }

    @GetMapping
    public ResponseEntity<List<FormulaResponse>> lister(HttpServletRequest request) {
        return ResponseEntity.ok(alimentationService.listerFormules(tenantId(request)));
    }

    private UUID tenantId(HttpServletRequest req) {
        return jwtService.extractTenantId(req.getHeader("Authorization").substring(7));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(
            @PathVariable UUID id, HttpServletRequest request) {
        alimentationService.supprimerFormule(id, tenantId(request));
        return ResponseEntity.noContent().build();
    }
}