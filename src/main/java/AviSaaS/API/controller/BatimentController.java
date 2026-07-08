package AviSaaS.API.controller;

import AviSaaS.API.dto.request.CreateBatimentRequest;
import AviSaaS.API.dto.request.UpdateBatimentRequest;
import AviSaaS.API.dto.response.BatimentResponse;
import AviSaaS.API.security.JwtService;
import AviSaaS.API.service.BatimentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("/api/v1/batiments")
@RequiredArgsConstructor
public class BatimentController extends BaseController {

    private final BatimentService batimentService;
    private final JwtService      jwtService;

    // POST /api/v1/batiments
    @PostMapping
    public ResponseEntity<BatimentResponse> creer(
            @Valid @RequestBody CreateBatimentRequest req,
            HttpServletRequest httpRequest) {
        System.out.println("=== CONTROLLER BANDE APPELE ===");
        String token    = extractToken(httpRequest);
        var    tenantId = jwtService.extractTenantId(token);

        return ResponseEntity
                .status(201)
                .body(batimentService.creer(req, tenantId));
    }

    private String extractToken(HttpServletRequest req) {
        String header = req.getHeader("Authorization");
        return header.substring(7);
    }


    @GetMapping
    public ResponseEntity<List<BatimentResponse>> lister(
            HttpServletRequest httpRequest) {

        String token = extractToken(httpRequest);
        var tenantId = jwtService.extractTenantId(token);

        return ResponseEntity.ok(
                batimentService.lister(tenantId)
        );
    }

    //modifier
    @PatchMapping("/{id}")
    public ResponseEntity<BatimentResponse> modifier(
            @PathVariable UUID id,
            @RequestBody UpdateBatimentRequest req,
            HttpServletRequest request) {

        return ResponseEntity.ok(
                batimentService.modifier(
                        id,
                        extractTenantId(request),
                        req
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(
            @PathVariable UUID id,
            HttpServletRequest request) {

        batimentService.supprimer(
                id,
                extractTenantId(request)
        );

        return ResponseEntity.noContent().build();
    }

}