package AviSaaS.API.controller;

import AviSaaS.API.dto.request.CreateProphylaxieRequest;
import AviSaaS.API.dto.response.ProphylaxieResponse;
import AviSaaS.API.entity.User; // 🟢 Remplace par le package exact de ton entité User
import AviSaaS.API.service.ProphylaxieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/bandes/{bandeId}/prophylaxie")
@RequiredArgsConstructor
public class ProphylaxieController {

    private final ProphylaxieService prophylaxieService;

    @PostMapping
    public ResponseEntity<ProphylaxieResponse> enregistrer(
            @PathVariable UUID bandeId,
            @Valid @RequestBody CreateProphylaxieRequest req,
            @AuthenticationPrincipal User currentUser) { // 🟢 

        UUID tenantId = currentUser.getTenant().getId();
        return ResponseEntity.status(201)
                .body(prophylaxieService.enregistrer(bandeId, tenantId, req));
    }

    @GetMapping
    public ResponseEntity<List<ProphylaxieResponse>> lister(
            @PathVariable UUID bandeId,
            @AuthenticationPrincipal User currentUser) { //

        UUID tenantId = currentUser.getTenant().getId();
        return ResponseEntity.ok(prophylaxieService.lister(bandeId, tenantId));
    }
}