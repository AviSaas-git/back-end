package AviSaaS.API.controller;

import AviSaaS.API.dto.request.CreateBandeRequest;
import AviSaaS.API.dto.response.BandeResponse;
import AviSaaS.API.entity.User; // 👈 Assure-toi d'importer ton entité User ou UserDetails
import AviSaaS.API.service.BandeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/bandes")
@RequiredArgsConstructor
public class BandeController {

    private final BandeService bandeService;

    @PostMapping
    public ResponseEntity<BandeResponse> creer(
            @Valid @RequestBody CreateBandeRequest req,
            @AuthenticationPrincipal User currentUser) { // 🟢 Injecté proprement par Spring

        System.out.println("POST /bandes appelé par: " + currentUser.getUsername());
        UUID tenantId = currentUser.getTenant().getId(); // 🟢 Plus besoin de parser le JWT !

        return ResponseEntity.status(201)
                .body(bandeService.creer(req, tenantId));
    }

    @GetMapping
    public ResponseEntity<List<BandeResponse>> lister(
            @AuthenticationPrincipal User currentUser) { // 🟢 Injecté proprement par Spring

        UUID tenantId = currentUser.getTenant().getId();
        return ResponseEntity.ok(bandeService.lister(tenantId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BandeResponse> getDetail(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) { // 🟢 Spring t'injecte l'utilisateur validé

        // Plus besoin de manipuler le header ou le JwtService !
        UUID tenantId = currentUser.getTenant().getId();

        return ResponseEntity.ok(bandeService.getDetail(id, tenantId));
    }
}