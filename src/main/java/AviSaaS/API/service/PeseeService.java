package AviSaaS.API.service;


import AviSaaS.API.dto.request.CreatePeseeRequest;
import AviSaaS.API.dto.response.PeseeResponse;
import AviSaaS.API.entity.Bande;
import AviSaaS.API.entity.PeseeBande;
import AviSaaS.API.entity.PoidsReference;
import AviSaaS.API.repository.BandeRepository;
import AviSaaS.API.repository.PeseeBandeRepository;
import AviSaaS.API.repository.PoidsReferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PeseeService {

    private final PeseeBandeRepository peseeRepository;
    private final BandeRepository bandeRepository;
    private final PoidsReferenceRepository poidsReferenceRepository;

    @Transactional
    public PeseeResponse enregistrer(UUID bandeId, UUID tenantId,
                                     CreatePeseeRequest req) {

        Bande bande = bandeRepository.findByIdAndTenantId(bandeId, tenantId)
                .orElseThrow(() -> new RuntimeException("Bande introuvable"));

        int ageEnJours = (int) ChronoUnit.DAYS.between(
                bande.getDateArrivee(), req.getDate());

        double poidsMoyenGrammes =
                (req.getPoidsTotalKg() * 1000) / req.getNombreSujetsPeses();

        PeseeBande pesee = PeseeBande.builder()
                .bande(bande)
                .date(req.getDate())
                .ageEnJours(ageEnJours)
                .nombreSujetsPeses(req.getNombreSujetsPeses())
                .poidsMoyenGrammes(poidsMoyenGrammes)
                .build();
        peseeRepository.save(pesee);

        return toResponse(pesee, bande);
    }

    public List<PeseeResponse> lister(UUID bandeId, UUID tenantId) {
        Bande bande = bandeRepository.findByIdAndTenantId(bandeId, tenantId)
                .orElseThrow(() -> new RuntimeException("Bande introuvable"));

        return peseeRepository.findByBandeIdOrderByDateAsc(bandeId)
                .stream()
                .map(p -> toResponse(p, bande))
                .toList();
    }

    private PeseeResponse toResponse(PeseeBande p, Bande bande) {
        Double cible = poidsReferenceRepository
                .findFirstByEspeceIdAndJourLessThanEqualOrderByJourDesc(
                        bande.getEspece().getId(), p.getAgeEnJours())
                .map(PoidsReference::getPoidsGrammesCible)
                .orElse(null);

        Double ecart = cible != null
                ? ((p.getPoidsMoyenGrammes() - cible) / cible) * 100
                : null;

        return PeseeResponse.builder()
                .id(p.getId())
                .date(p.getDate())
                .ageEnJours(p.getAgeEnJours())
                .nombreSujetsPeses(p.getNombreSujetsPeses())
                .poidsMoyenGrammes(p.getPoidsMoyenGrammes())
                .poidsCibleGrammes(cible)
                .ecartPct(ecart)
                .build();
    }
}