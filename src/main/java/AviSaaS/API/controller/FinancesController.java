package AviSaaS.API.controller;



import AviSaaS.API.dto.request.CreateDepenseRequest;
import AviSaaS.API.dto.response.DepenseResponse;
import AviSaaS.API.dto.response.FinancesBandeResponse;
import AviSaaS.API.security.JwtService;
import AviSaaS.API.service.FinancesService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/bandes/{bandeId}/finances")
@RequiredArgsConstructor
public class FinancesController {

    private final FinancesService financesService;
    private final JwtService      jwtService;

    @PostMapping
    public ResponseEntity<DepenseResponse> ajouterDepense(
            @PathVariable UUID bandeId,
            @Valid @RequestBody CreateDepenseRequest req,
            HttpServletRequest request) {
        return ResponseEntity.status(201)
                .body(financesService.ajouterDepense(bandeId, tenantId(request), req));
    }

    @GetMapping
    public ResponseEntity<FinancesBandeResponse> getFinances(
            @PathVariable UUID bandeId, HttpServletRequest request) {
        return ResponseEntity.ok(
                financesService.getFinances(bandeId, tenantId(request)));
    }

    private UUID tenantId(HttpServletRequest req) {
        return jwtService.extractTenantId(req.getHeader("Authorization").substring(7));
    }
}