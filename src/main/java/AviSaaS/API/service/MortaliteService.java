package AviSaaS.API.service;



import AviSaaS.API.dto.request.CreateMortaliteRequest;
import AviSaaS.API.dto.response.MortaliteResponse;
import AviSaaS.API.entity.Bande;
import AviSaaS.API.entity.SuiviMortalite;
import AviSaaS.API.repository.BandeRepository;
import AviSaaS.API.repository.SuiviMortaliteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MortaliteService {

    private final SuiviMortaliteRepository mortaliteRepository;
    private final BandeRepository bandeRepository;

    @Transactional
    public MortaliteResponse enregistrer(UUID bandeId, UUID tenantId,
                                         CreateMortaliteRequest req) {

        Bande bande = bandeRepository.findByIdAndTenantId(bandeId, tenantId)
                .orElseThrow(() -> new RuntimeException("Bande introuvable"));

        if (req.getNombreMorts() > bande.getEffectifActuel()) {
            throw new RuntimeException(
                    "Dépasse l'effectif actuel (" + bande.getEffectifActuel() + ")");
        }

        SuiviMortalite mortalite = SuiviMortalite.builder()
                .bande(bande)
                .date(req.getDate())
                .nombreMorts(req.getNombreMorts())
                .cause(req.getCause())
                .build();
        mortaliteRepository.save(mortalite);

        bande.setEffectifActuel(bande.getEffectifActuel() - req.getNombreMorts());
        bandeRepository.save(bande);

        return toResponse(mortalite);
    }

    public List<MortaliteResponse> lister(UUID bandeId, UUID tenantId) {
        bandeRepository.findByIdAndTenantId(bandeId, tenantId)
                .orElseThrow(() -> new RuntimeException("Bande introuvable"));
        return mortaliteRepository.findByBandeIdOrderByDateDesc(bandeId)
                .stream().map(this::toResponse).toList();
    }

    private MortaliteResponse toResponse(SuiviMortalite m) {
        return MortaliteResponse.builder()
                .id(m.getId()).date(m.getDate())
                .nombreMorts(m.getNombreMorts()).cause(m.getCause())
                .build();
    }
}