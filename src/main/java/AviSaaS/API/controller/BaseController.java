package AviSaaS.API.controller;

import AviSaaS.API.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

public abstract class BaseController {

    @Autowired
    protected JwtService jwtService;

    protected UUID extractTenantId(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        return jwtService.extractTenantId(token);
    }
}