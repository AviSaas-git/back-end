package AviSaaS.API.service;


import AviSaaS.API.dto.request.CreateFermeRequest;
import AviSaaS.API.dto.response.BandeResponse;
import AviSaaS.API.dto.response.FermeResponse;
import AviSaaS.API.entity.Ferme;
import AviSaaS.API.entity.Tenant;
import AviSaaS.API.repository.FermeRepository;
import AviSaaS.API.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FermeService {

    private final FermeRepository  fermeRepository;
    private final TenantRepository tenantRepository;

    @Transactional
    public FermeResponse creer(CreateFermeRequest req, UUID tenantId) {

        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant introuvable"));

        Ferme ferme = Ferme.builder()
                .tenant(tenant)
                .nom(req.getNom())
                .localisation(req.getLocalisation())
                .surfaceM2(req.getSurfaceM2())
                .capaciteMax(req.getCapaciteMax())
                .build();

        ferme = fermeRepository.save(ferme);
        return toResponse(ferme);
    }

    public List<FermeResponse> lister(UUID tenantId) {

        return fermeRepository
                .findByTenantId(tenantId)
                .stream()
                .map(this::toResponse)
                .toList();
    }
    private FermeResponse toResponse(Ferme f) {
        return FermeResponse.builder()
                .id(f.getId())
                .nom(f.getNom())
                .localisation(f.getLocalisation())
                .surfaceM2(f.getSurfaceM2())
                .capaciteMax(f.getCapaciteMax())
                .tenantId(f.getTenant().getId())
                .build();
    }
}