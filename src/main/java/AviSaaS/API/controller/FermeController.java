package AviSaaS.API.controller;



import AviSaaS.API.dto.request.CreateFermeRequest;
import AviSaaS.API.dto.response.BandeResponse;
import AviSaaS.API.dto.response.FermeResponse;
import AviSaaS.API.security.JwtService;
import AviSaaS.API.service.FermeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/fermes")
@RequiredArgsConstructor
public class FermeController {

    private final FermeService fermeService;
    private final JwtService   jwtService;

    // POST /api/v1/fermes
    @PostMapping
    public ResponseEntity<FermeResponse> creer(
            @Valid @RequestBody CreateFermeRequest req,
            HttpServletRequest httpRequest) {

        // Extraire le tenantId depuis le JWT
        String token    = extractToken(httpRequest);
        var    tenantId = jwtService.extractTenantId(token);

        return ResponseEntity
                .status(201)
                .body(fermeService.creer(req, tenantId));
    }


    @GetMapping
    public ResponseEntity<List<FermeResponse>> lister(
            HttpServletRequest request) {

        var tenantId = extractTenantId(request);
        return ResponseEntity.ok(fermeService.lister(tenantId));
    }

    private String extractToken(HttpServletRequest req) {
        String header = req.getHeader("Authorization");
        return header.substring(7); // retire "Bearer "
    }

    private java.util.UUID extractTenantId(HttpServletRequest req) {
        String token = req.getHeader("Authorization").substring(7);
        return jwtService.extractTenantId(token);
    }
}