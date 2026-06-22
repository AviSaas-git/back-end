package AviSaaS.API.service;


import AviSaaS.API.dto.request.CourbeReferenceRequest;
import AviSaaS.API.dto.response.CourbePointResponse;
import AviSaaS.API.entity.EspeceReference;
import AviSaaS.API.entity.PoidsReference;
import AviSaaS.API.repository.EspeceReferenceRepository;
import AviSaaS.API.repository.PoidsReferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CourbeReferenceService {

    private final PoidsReferenceRepository  poidsReferenceRepository;
    private final EspeceReferenceRepository especeRepository;

    @Transactional
    public void enregistrerCourbe(UUID especeId, CourbeReferenceRequest req) {

        EspeceReference espece = especeRepository.findById(especeId)
                .orElseThrow(() -> new RuntimeException("Espèce introuvable"));

        // On remplace entièrement la courbe existante par la nouvelle saisie
        poidsReferenceRepository.deleteByEspeceId(especeId);

        List<PoidsReference> points = req.getPoints().stream()
                .map(p -> PoidsReference.builder()
                        .espece(espece)
                        .jour(p.getJour())
                        .poidsGrammesCible(p.getPoidsGrammesCible())
                        .build())
                .toList();

        poidsReferenceRepository.saveAll(points);
    }

    public List<CourbePointResponse> getCourbe(UUID especeId) {
        return poidsReferenceRepository.findByEspeceIdOrderByJourAsc(especeId)
                .stream()
                .map(p -> CourbePointResponse.builder()
                        .jour(p.getJour())
                        .poidsCibleGrammes(p.getPoidsGrammesCible())
                        .poidsReelGrammes(null)
                        .build())
                .toList();
    }
}