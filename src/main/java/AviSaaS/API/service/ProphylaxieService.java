package AviSaaS.API.service;


import AviSaaS.API.dto.request.CreateProphylaxieRequest;
import AviSaaS.API.dto.response.ProphylaxieResponse;
import AviSaaS.API.entity.Bande;
import AviSaaS.API.entity.SuiviProphylaxie;
import AviSaaS.API.repository.BandeRepository;
import AviSaaS.API.repository.SuiviProphylaxieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProphylaxieService {

    private final SuiviProphylaxieRepository prophylaxieRepository;
    private final BandeRepository bandeRepository;

    @Transactional
    public ProphylaxieResponse enregistrer(UUID bandeId, UUID tenantId,
                                           CreateProphylaxieRequest req) {

        Bande bande = bandeRepository.findByIdAndTenantId(bandeId, tenantId)
                .orElseThrow(() -> new RuntimeException("Bande introuvable"));

        SuiviProphylaxie.VoieAdministration voie = null;
        if (req.getVoieAdministration() != null && !req.getVoieAdministration().isBlank()) {
            try {
                voie = SuiviProphylaxie.VoieAdministration.valueOf(req.getVoieAdministration());
            } catch (IllegalArgumentException ignored) {}
        }

        SuiviProphylaxie fiche = SuiviProphylaxie.builder()
                .bande(bande)
                .dateApplication(req.getDateApplication())
                .traitement(req.getTraitement())
                .laboratoire(req.getLaboratoire())
                .dosage(req.getDosage())
                .voieAdministration(voie)
                .observations(req.getObservations())
                .build();

        prophylaxieRepository.save(fiche);
        return toResponse(fiche);
    }

    public List<ProphylaxieResponse> lister(UUID bandeId, UUID tenantId) {
        bandeRepository.findByIdAndTenantId(bandeId, tenantId)
                .orElseThrow(() -> new RuntimeException("Bande introuvable"));
        return prophylaxieRepository.findByBandeIdOrderByDateApplicationAsc(bandeId)
                .stream().map(this::toResponse).toList();
    }

    private ProphylaxieResponse toResponse(SuiviProphylaxie f) {
        return ProphylaxieResponse.builder()
                .id(f.getId())
                .dateApplication(f.getDateApplication())
                .ageEnJoursAuTraitement(f.getAgeEnJoursAuTraitement())
                .traitement(f.getTraitement())
                .laboratoire(f.getLaboratoire())
                .dosage(f.getDosage())
                .voieAdministration(f.getVoieAdministration() != null
                        ? f.getVoieAdministration().name() : null)
                .observations(f.getObservations())
                .build();
    }
}