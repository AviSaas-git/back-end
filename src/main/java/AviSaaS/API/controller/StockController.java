package AviSaaS.API.controller;

import AviSaaS.API.dto.request.CreateStockItemRequest;
import AviSaaS.API.dto.request.MouvementStockRequest;
import AviSaaS.API.dto.response.StockItemResponse;
import AviSaaS.API.security.JwtService;
import AviSaaS.API.service.StockService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/stock")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;
    private final JwtService jwtService;

    @PostMapping
    public ResponseEntity<StockItemResponse> creer(
            @Valid @RequestBody CreateStockItemRequest req, HttpServletRequest request) {
        return ResponseEntity.status(201).body(stockService.creer(req, tenantId(request)));
    }

    @GetMapping
    public ResponseEntity<List<StockItemResponse>> lister(HttpServletRequest request) {
        return ResponseEntity.ok(stockService.lister(tenantId(request)));
    }

    @PostMapping("/{itemId}/mouvements")
    public ResponseEntity<StockItemResponse> mouvement(
            @PathVariable UUID itemId,
            @Valid @RequestBody MouvementStockRequest req,
            HttpServletRequest request) {
        return ResponseEntity.ok(
                stockService.enregistrerMouvement(itemId, tenantId(request), req));
    }

    private UUID tenantId(HttpServletRequest req) {
        return jwtService.extractTenantId(req.getHeader("Authorization").substring(7));
    }
}