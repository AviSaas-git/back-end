package AviSaaS.API.service;


import AviSaaS.API.dto.request.CreateBatimentRequest;
import AviSaaS.API.dto.response.BatimentResponse;
import AviSaaS.API.entity.Batiment;
import AviSaaS.API.entity.Ferme;
import AviSaaS.API.entity.ModeOccupation;
import AviSaaS.API.repository.BatimentRepository;
import AviSaaS.API.repository.FermeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BatimentService {

    private final BatimentRepository batimentRepository;
    private final FermeRepository    fermeRepository;

    @Transactional
    public BatimentResponse creer(CreateBatimentRequest req, UUID tenantId) {

        // Vérifie que la ferme appartient bien au tenant
        Ferme ferme = fermeRepository
                .findByIdAndTenantId(req.getFermeId(), tenantId)
                .orElseThrow(() -> new RuntimeException("Ferme introuvable"));

        // Vérifie que la capacité du bâtiment
        // ne dépasse pas celle de la ferme
        if (req.getCapacite() > ferme.getCapaciteMax()) {
            throw new RuntimeException(
                    "Capacité bâtiment (" + req.getCapacite() +
                            ") dépasse la capacité ferme (" + ferme.getCapaciteMax() + ")"
            );
        }

        Batiment batiment = Batiment.builder()
                .ferme(ferme)
                .nom(req.getNom())
                .capacite(req.getCapacite())
                .surfaceM2(req.getSurfaceM2())
                .typeChauffage(req.getTypeChauffage())
                .type(req.getType() != null ? req.getType() : "poulailler")
                .modeOccupation(ModeOccupation.LIBRE)
                .build();

        batiment = batimentRepository.save(batiment);
        return toResponse(batiment);
    }

    private BatimentResponse toResponse(Batiment b) {
        return BatimentResponse.builder()
                .id(b.getId())
                .nom(b.getNom())
                .capacite(b.getCapacite())
                .surfaceM2(b.getSurfaceM2())
                .typeChauffage(b.getTypeChauffage())
                .type(b.getType())
                .fermeId(b.getFerme().getId())
                .build();
    }

    public List<BatimentResponse> listerParTenant(UUID tenantId) {

        return batimentRepository
                .findByFermeTenantId(tenantId)
                .stream()
                .map(this::toResponse)
                .toList();
    }
}