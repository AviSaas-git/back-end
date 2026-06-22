package AviSaaS.API.service;

import AviSaaS.API.dto.request.LoginRequest;
import AviSaaS.API.dto.request.RegisterRequest;
import AviSaaS.API.dto.response.AuthResponse;
import AviSaaS.API.entity.Tenant;
import AviSaaS.API.entity.User;
import AviSaaS.API.repository.TenantRepository;
import AviSaaS.API.repository.UserRepository;
import AviSaaS.API.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository       userRepository;
    private final TenantRepository     tenantRepository;
    private final PasswordEncoder      passwordEncoder;
    private final JwtService           jwtService;
    private final AuthenticationManager authenticationManager;

    // ── INSCRIPTION ───────────────────────────────────────────────────
    @Transactional
    public AuthResponse register(RegisterRequest req) {

        // 1. Vérifier que l'email n'existe pas déjà
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new RuntimeException("Email déjà utilisé : " + req.getEmail());
        }

        // 2. Créer le tenant (l'espace SaaS de l'éleveur)
        Tenant.PlanType planType = parsePlan(req.getPlan());
        int maxAnimaux = getMaxAnimaux(planType);

        Tenant tenant = Tenant.builder()
                .nom(req.getNom())
                .plan(planType)
                .actif(true)
                .maxAnimaux(maxAnimaux)
                .build();
        tenant = tenantRepository.save(tenant);

        // 3. Créer l'utilisateur lié au tenant
        User user = User.builder()
                .tenant(tenant)
                .nom(req.getNom())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .telephone(req.getTelephone())
                .role(User.RoleUser.OWNER)
                .actif(true)
                .build();
        user = userRepository.save(user);

        // 4. Générer le JWT
        String token = jwtService.generateToken(user, tenant.getId());

        return buildAuthResponse(user, tenant, token);
    }

    // ── CONNEXION ─────────────────────────────────────────────────────
    public AuthResponse login(LoginRequest req) {

        // 1. Spring Security vérifie email + mot de passe
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        req.getEmail(),
                        req.getPassword()
                )
        );

        // 2. Charger l'utilisateur
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        // 3. Générer le JWT
        String token = jwtService.generateToken(user, user.getTenant().getId());

        return buildAuthResponse(user, user.getTenant(), token);
    }

    // ── MÉTHODES PRIVÉES ──────────────────────────────────────────────
    private AuthResponse buildAuthResponse(User user, Tenant tenant,
                                           String token) {
        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .nom(user.getNom())
                .email(user.getEmail())
                .role(user.getRole().name())
                .tenantId(tenant.getId())
                .plan(tenant.getPlan().name())
                .build();
    }

    private Tenant.PlanType parsePlan(String plan) {
        if (plan == null) return Tenant.PlanType.FREE;
        try {
            return Tenant.PlanType.valueOf(plan.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Tenant.PlanType.FREE;
        }
    }

    private int getMaxAnimaux(Tenant.PlanType plan) {
        return switch (plan) {
            case FREE       -> 500;
            case PRO        -> 20_000;
            case ENTERPRISE -> Integer.MAX_VALUE;
        };
    }
}