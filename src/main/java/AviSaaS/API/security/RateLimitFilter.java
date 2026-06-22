package AviSaaS.API.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class RateLimitFilter extends OncePerRequestFilter {

    private static final int MAX_PAR_MINUTE = 10;
    private final ConcurrentHashMap<String, Fenetre> compteurs = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getServletPath();
        boolean routeSensible = path.equals("/api/v1/auth/login")
                || path.equals("/api/v1/auth/register");

        if (!routeSensible) {
            filterChain.doFilter(request, response);
            return;
        }

        String ip = request.getRemoteAddr();
        Fenetre fenetre = compteurs.computeIfAbsent(ip, k -> new Fenetre());

        if (fenetre.expiree()) fenetre.reinitialiser();

        if (fenetre.compteur.incrementAndGet() > MAX_PAR_MINUTE) {
            response.setStatus(429);
            response.setContentType("application/json");
            response.getWriter().write(
                    "{\"message\":\"Trop de tentatives. Réessayez dans une minute.\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private static class Fenetre {
        AtomicInteger compteur = new AtomicInteger(0);
        long debut = System.currentTimeMillis();
        boolean expiree() { return System.currentTimeMillis() - debut > 60_000; }
        void reinitialiser() { compteur.set(0); debut = System.currentTimeMillis(); }
    }
}