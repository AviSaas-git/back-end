package AviSaaS.API.service;

import AviSaaS.API.dto.request.CreateBandeRequest;
import AviSaaS.API.dto.response.BandeResponse;
import AviSaaS.API.entity.*;
import AviSaaS.API.exception.BusinessException;
import AviSaaS.API.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BandeService {

    private final BandeRepository bandeRepository;
    private final EspeceReferenceRepository especeRepo;
    private final BatimentRepository batimentRepository;
    private final TenantRepository tenantRepository;

    @Transactional
    public BandeResponse creer(CreateBandeRequest req, UUID tenantId) {

        // =====================================================
        // 1. Charger le tenant
        // =====================================================
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() ->
                        new RuntimeException("Tenant introuvable"));

        // =====================================================
        // 2. Charger l'espèce
        // =====================================================
        EspeceReference espece = especeRepo.findById(req.getEspeceId())
                .orElseThrow(() ->
                        new RuntimeException("Espèce introuvable"));

        // =====================================================
        // 3. Charger le bâtiment
        // =====================================================
        Batiment batiment = batimentRepository
                .findByIdAndFermeTenantId(
                        req.getBatimentId(),
                        tenantId
                )
                .orElseThrow(() ->
                        new RuntimeException("Bâtiment introuvable"));

        // =====================================================
        // 4. Vérifier le mode d'occupation
        // =====================================================
        if (batiment.getModeOccupation() == ModeOccupation.INDIVIDUEL) {
            throw new BusinessException(
                    "Ce bâtiment contient déjà des animaux individuels"
            );
        }

        // =====================================================
        // 5. Vérifier qu'aucune bande active n'existe
        // =====================================================
        boolean bandeActiveExiste =
                bandeRepository.existsByBatimentIdAndStatut(
                        batiment.getId(),
                        Bande.StatutBande.ACTIVE
                );

        if (bandeActiveExiste) {
            throw new RuntimeException(
                    "Une bande active existe déjà dans ce bâtiment"
            );
        }

        // =====================================================
        // 6. Vérifier la capacité
        // =====================================================
        if (req.getEffectifInitial() > batiment.getCapacite()) {

            throw new BusinessException(
                    "Effectif (" + req.getEffectifInitial() +
                            ") dépasse la capacité du bâtiment (" +
                            batiment.getCapacite() + ")"
            );
        }

        // =====================================================
        // 7. Génération référence unique
        // =====================================================
        int year = req.getDateArrivee().getYear();

        LocalDate startOfYear =
                LocalDate.of(year, 1, 1);

        LocalDate endOfYear =
                LocalDate.of(year, 12, 31);

        long totalDeLAnnee =
                bandeRepository.countByTenantIdAndDateArriveeBetween(
                        tenantId,
                        startOfYear,
                        endOfYear
                );

        String uniqueId =
                UUID.randomUUID()
                        .toString()
                        .substring(0, 4)
                        .toUpperCase();

        String reference = String.format(
                "BAND-%d-%03d-%s",
                year,
                totalDeLAnnee + 1,
                uniqueId
        );

        // =====================================================
        // 8. Créer la bande
        // =====================================================
        Bande bande = Bande.builder()
                .tenant(tenant)
                .espece(espece)
                .batiment(batiment)
                .reference(reference)
                .race(req.getRace())
                .effectifInitial(req.getEffectifInitial())
                .effectifActuel(req.getEffectifInitial())
                .dateArrivee(req.getDateArrivee())
                .fournisseur(req.getFournisseur())
                .statut(Bande.StatutBande.ACTIVE)
                .build();

        // =====================================================
        // 9. Marquer le bâtiment comme occupé par une bande
        // =====================================================
        batiment.setModeOccupation(ModeOccupation.BANDE);
        batimentRepository.save(batiment);

        // =====================================================
        // 10. Sauvegarder la bande
        // =====================================================
        bande = bandeRepository.save(bande);

        return toResponse(bande);
    }

    public List<BandeResponse> lister(UUID tenantId) {

        return bandeRepository
                .findByTenantIdOrderByCreatedAtDesc(tenantId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private BandeResponse toResponse(Bande b) {

        return BandeResponse.builder()
                .id(b.getId())
                .reference(b.getReference())
                .especeNom(b.getEspece().getNom())
                .especeIcon(b.getEspece().getIcon())
                .especeMode(
                        b.getEspece()
                                .getModeGestion()
                                .name()
                )
                .batimentNom(b.getBatiment().getNom())
                .race(b.getRace())
                .effectifInitial(b.getEffectifInitial())
                .effectifActuel(b.getEffectifActuel())
                .dateArrivee(b.getDateArrivee())
                .statut(b.getStatut().name())
                .tauxMortalite(b.getTauxMortalite())
                .ageEnJours(b.getAgeEnJours())
                .progressionPct(b.getProgressionPct())
                .especeId(b.getEspece().getId())
                .build();
    }


    public BandeResponse getDetail(UUID id, UUID tenantId) {
        Bande bande = bandeRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new RuntimeException("Bande introuvable"));
        return toResponse(bande);
    }
}