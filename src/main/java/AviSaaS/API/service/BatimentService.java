package AviSaaS.API.service;

import AviSaaS.API.dto.request.CreateBatimentRequest;
import AviSaaS.API.dto.request.UpdateBatimentRequest;
import AviSaaS.API.dto.response.BatimentResponse;
import AviSaaS.API.entity.Bande;
import AviSaaS.API.entity.Batiment;
import AviSaaS.API.entity.Ferme;
import AviSaaS.API.repository.BandeRepository;
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
    private final BandeRepository    bandeRepository;

    // ── CRÉER ─────────────────────────────────────────────────────────
    @Transactional
    public BatimentResponse creer(CreateBatimentRequest req, UUID tenantId) {

        Ferme ferme = fermeRepository
                .findByIdAndTenantId(req.getFermeId(), tenantId)
                .orElseThrow(() -> new RuntimeException("Ferme introuvable"));

        if (req.getCapacite() > ferme.getCapaciteMax()) {
            throw new RuntimeException(
                    "Capacité bâtiment (" + req.getCapacite() +
                            ") dépasse la capacité de la ferme (" +
                            ferme.getCapaciteMax() + ")");
        }

        Batiment batiment = Batiment.builder()
                .ferme(ferme)
                .nom(req.getNom())
                .capacite(req.getCapacite())
                .surfaceM2(req.getSurfaceM2())
                .typeChauffage(req.getTypeChauffage())
                .type(req.getType() != null ? req.getType() : "poulailler")
                .build();

        return toResponse(batimentRepository.save(batiment));
    }

    // ── LISTER ────────────────────────────────────────────────────────
    public List<BatimentResponse> lister(UUID tenantId) {
        return batimentRepository.findByFermeTenantId(tenantId)
                .stream().map(this::toResponse).toList();
    }

    // ── MODIFIER ──────────────────────────────────────────────────────
    @Transactional
    public BatimentResponse modifier(UUID id, UUID tenantId,
                                     UpdateBatimentRequest req) {

        Batiment bat = batimentRepository
                .findByIdAndFermeTenantId(id, tenantId)
                .orElseThrow(() -> new RuntimeException("Bâtiment introuvable"));

        if (req.getNom()           != null) bat.setNom(req.getNom());
        if (req.getCapacite()      != null) bat.setCapacite(req.getCapacite());
        if (req.getSurfaceM2()     != null) bat.setSurfaceM2(req.getSurfaceM2());
        if (req.getTypeChauffage() != null) bat.setTypeChauffage(req.getTypeChauffage());
        if (req.getType()          != null) bat.setType(req.getType());

        return toResponse(batimentRepository.save(bat));
    }

    // ── SUPPRIMER ─────────────────────────────────────────────────────
    @Transactional
    public void supprimer(UUID id, UUID tenantId) {

        Batiment bat = batimentRepository
                .findByIdAndFermeTenantId(id, tenantId)
                .orElseThrow(() -> new RuntimeException("Bâtiment introuvable"));

        boolean existe = bandeRepository
                .existsByBatimentIdAndTenantIdAndStatut(
                        id,
                        tenantId,
                        Bande.StatutBande.ACTIVE
                );

        if (existe) {
            throw new RuntimeException(
                    "Impossible de supprimer ce bâtiment : une bande active y est encore présente."
            );
        }

        batimentRepository.delete(bat);
    }
    // ── MAPPER ────────────────────────────────────────────────────────
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

}