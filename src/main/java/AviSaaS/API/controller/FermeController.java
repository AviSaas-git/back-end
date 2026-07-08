package AviSaaS.API.controller;



import AviSaaS.API.dto.request.CreateFermeRequest;
import AviSaaS.API.dto.request.UpdateFermeRequest;
import AviSaaS.API.dto.response.BandeResponse;
import AviSaaS.API.dto.response.FermeResponse;
import AviSaaS.API.security.JwtService;
import AviSaaS.API.service.FermeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("/api/v1/fermes")
@RequiredArgsConstructor
public class FermeController extends BaseController {

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

// modifier
    @PatchMapping("/{id}")
    public ResponseEntity<FermeResponse> modifier(
            @PathVariable UUID id,
            @RequestBody UpdateFermeRequest req,
            HttpServletRequest request) {

        UUID tenantId = extractTenantId(request);

        return ResponseEntity.ok(
                fermeService.modifier(id, tenantId, req)
        );
    }

    //delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(
            @PathVariable UUID id,
            HttpServletRequest request) {

        fermeService.supprimer(id, extractTenantId(request));
        return ResponseEntity.noContent().build();
    }
}