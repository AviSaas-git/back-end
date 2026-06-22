package AviSaaS.API.service;

import AviSaaS.API.dto.response.BatimentResponse;
import AviSaaS.API.dto.response.DashboardResponse;
import AviSaaS.API.dto.response.FermeResponse;
import AviSaaS.API.entity.Animal;
import AviSaaS.API.entity.Bande;
import AviSaaS.API.entity.Batiment;
import AviSaaS.API.entity.Ferme;
import AviSaaS.API.repository.AnimalRepository;
import AviSaaS.API.repository.BandeRepository;
import AviSaaS.API.repository.BatimentRepository;
import AviSaaS.API.repository.FermeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final FermeRepository    fermeRepository;
    private final BatimentRepository batimentRepository;
    private final BandeRepository bandeRepository;
    private final AnimalRepository animalRepository;
    public DashboardResponse getDashboard(UUID tenantId) {

        List<Ferme>    fermes    = fermeRepository.findByTenantId(tenantId);
        List<Batiment> batiments = batimentRepository.findByFermeTenantId(tenantId);

        return DashboardResponse.builder()
                .totalFermes(fermes.size())
                .totalBatiments(batiments.size())
                .totalBandesActives(
                        bandeRepository.countByTenantIdAndStatut(
                                tenantId, Bande.StatutBande.ACTIVE))
                .totalAnimauxIndividuels(
                        animalRepository.countByTenantIdAndStatut(
                                tenantId, Animal.StatutAnimal.ACTIF))
                .fermes(fermes.stream().map(this::toFermeResponse).toList())
                .batiments(batiments.stream().map(this::toBatimentResponse).toList())
                .build();
    }

    private FermeResponse toFermeResponse(Ferme f) {
        return FermeResponse.builder()
                .id(f.getId())
                .nom(f.getNom())
                .localisation(f.getLocalisation())
                .surfaceM2(f.getSurfaceM2())
                .capaciteMax(f.getCapaciteMax())
                .tenantId(f.getTenant().getId())
                .build();
    }

    private BatimentResponse toBatimentResponse(Batiment b) {
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
}