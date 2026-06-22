package AviSaaS.API.config;

import AviSaaS.API.entity.EspeceReference;
import AviSaaS.API.repository.EspeceReferenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final EspeceReferenceRepository especeRepo;

    @Bean
    public ApplicationRunner initEspeces() {
        return args -> {
            if (especeRepo.count() > 0) return; // déjà initialisé

            List<EspeceReference> especes = List.of(
                    EspeceReference.builder()
                            .nom("Poulet Cobb 500")
                            .icon("🐔")
                            .modeGestion(EspeceReference.ModeGestion.LOT)
                            .cycleMoyenJours(63)
                            .gererReproduction(false)
                            .build(),
                    EspeceReference.builder()
                            .nom("Poule pondeuse")
                            .icon("🐓")
                            .modeGestion(EspeceReference.ModeGestion.LOT)
                            .cycleMoyenJours(0)
                            .gererReproduction(false)
                            .build(),
                    EspeceReference.builder()
                            .nom("Dinde")
                            .icon("🦃")
                            .modeGestion(EspeceReference.ModeGestion.LOT)
                            .cycleMoyenJours(84)
                            .gererReproduction(false)
                            .build(),
                    EspeceReference.builder()
                            .nom("Porc Large White")
                            .icon("🐷")
                            .modeGestion(EspeceReference.ModeGestion.INDIVIDUEL)
                            .dureeGestationJours(114)
                            .gererReproduction(true)
                            .build(),
                    EspeceReference.builder()
                            .nom("Lapin NZ Blanc")
                            .icon("🐰")
                            .modeGestion(EspeceReference.ModeGestion.INDIVIDUEL)
                            .dureeGestationJours(31)
                            .gererReproduction(true)
                            .build()
            );

            especeRepo.saveAll(especes);
            log.info("✅ {} espèces de référence initialisées", especes.size());
        };
    }
}