package AviSaaS.API.service;


import AviSaaS.API.dto.response.CourbePointResponse;
import AviSaaS.API.entity.Bande;
import AviSaaS.API.entity.PeseeBande;
import AviSaaS.API.entity.PoidsReference;
import AviSaaS.API.repository.BandeRepository;
import AviSaaS.API.repository.PeseeBandeRepository;
import AviSaaS.API.repository.PoidsReferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.TreeMap;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CourbeCroissanceService {

    private final BandeRepository           bandeRepository;
    private final PoidsReferenceRepository  poidsReferenceRepository;
    private final PeseeBandeRepository      peseeRepository;

    public List<CourbePointResponse> getCourbe(UUID bandeId, UUID tenantId) {

        Bande bande = bandeRepository.findByIdAndTenantId(bandeId, tenantId)
                .orElseThrow(() -> new RuntimeException("Bande introuvable"));

        UUID especeId = bande.getEspece().getId();

        // TreeMap garde les jours triés automatiquement
        TreeMap<Integer, CourbePointResponse> points = new TreeMap<>();

        // 1. Courbe théorique du fournisseur
        for (PoidsReference ref : poidsReferenceRepository
                .findByEspeceIdOrderByJourAsc(especeId)) {
            points.put(ref.getJour(), CourbePointResponse.builder()
                    .jour(ref.getJour())
                    .poidsCibleGrammes(ref.getPoidsGrammesCible())
                    .poidsReelGrammes(null)
                    .build());
        }

        // 2. Pesées réelles — fusionnées sur le même jour si déjà présent
        for (PeseeBande pesee : peseeRepository
                .findByBandeIdOrderByDateAsc(bandeId)) {
            int jour = pesee.getAgeEnJours();
            CourbePointResponse existant = points.get(jour);

            points.put(jour, CourbePointResponse.builder()
                    .jour(jour)
                    .poidsCibleGrammes(existant != null
                            ? existant.getPoidsCibleGrammes() : null)
                    .poidsReelGrammes(pesee.getPoidsMoyenGrammes())
                    .build());
        }

        return List.copyOf(points.values());
    }
}