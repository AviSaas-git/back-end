package AviSaaS.API.controller;

import AviSaaS.API.dto.response.DashboardResponse;
import AviSaaS.API.security.JwtService;
import AviSaaS.API.service.DashboardService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final JwtService       jwtService;

    @GetMapping
    public ResponseEntity<DashboardResponse> getDashboard(
            HttpServletRequest request) {

        String token    = request.getHeader("Authorization").substring(7);
        var    tenantId = jwtService.extractTenantId(token);

        return ResponseEntity.ok(dashboardService.getDashboard(tenantId));
    }
}