package AviSaaS.API.service;

import AviSaaS.API.dto.request.CreateAnimalRequest;
import AviSaaS.API.dto.response.AnimalResponse;
import AviSaaS.API.entity.*;
import AviSaaS.API.repository.*;
import AviSaaS.API.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AnimalService {

    private final AnimalRepository         animalRepository;
    private final EspeceReferenceRepository especeRepo;
    private final BatimentRepository       batimentRepository;
    private final TenantRepository         tenantRepository;

    @Transactional
    public AnimalResponse creer(CreateAnimalRequest req, UUID tenantId) {

        // Vérification numéro unique
        if (animalRepository.existsByNumeroAndTenantId(req.getNumero(), tenantId)) {
            throw new RuntimeException(
                    "Numéro déjà utilisé : " + req.getNumero());
        }

        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant introuvable"));

        EspeceReference espece = especeRepo.findById(req.getEspeceId())
                .orElseThrow(() -> new RuntimeException("Espèce introuvable"));

        Batiment batiment = batimentRepository
                .findByIdAndFermeTenantId(req.getBatimentId(), tenantId)
                .orElseThrow(() -> new RuntimeException("Bâtiment introuvable"));

        // Généalogie optionnelle
        Animal pere = null;
        Animal mere = null;
        if (req.getPereId() != null) {
            pere = animalRepository.findByIdAndTenantId(req.getPereId(), tenantId)
                    .orElseThrow(() -> new RuntimeException("Père introuvable ou n'appartient pas à votre élevage"));
        }
        if (req.getMereId() != null) {
            mere = animalRepository.findByIdAndTenantId(req.getMereId(), tenantId)
                    .orElseThrow(() -> new RuntimeException("Mère introuvable ou n'appartient pas à votre élevage"));
        }
        Animal animal = Animal.builder()
                .tenant(tenant)
                .espece(espece)
                .batiment(batiment)
                .numero(req.getNumero())
                .nom(req.getNom())
                .sexe(Animal.SexeEnum.valueOf(req.getSexe()))
                .dateNaissance(req.getDateNaissance())
                .origine(Animal.OrigineEnum.valueOf(req.getOrigine()))
                .poidsActuelKg(req.getPoidsActuelKg())
                .pere(pere)
                .mere(mere)
                .statut(Animal.StatutAnimal.ACTIF)
                .build();

        animal = animalRepository.save(animal);
        return toResponse(animal);
    }

    public List<AnimalResponse> lister(UUID tenantId) {
        return animalRepository
                .findByTenantIdOrderByCreatedAtDesc(tenantId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Récupère les reproducteurs d'une espèce par sexe
    public List<AnimalResponse> getReproducteurs(
            UUID tenantId, UUID especeId, String sexe) {
        return animalRepository
                .findByTenantIdAndEspeceIdAndSexe(
                        tenantId, especeId,
                        Animal.SexeEnum.valueOf(sexe))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private AnimalResponse toResponse(Animal a) {
        return AnimalResponse.builder()
                .id(a.getId())
                .numero(a.getNumero())
                .nom(a.getNom())
                .sexe(a.getSexe().name())
                .dateNaissance(a.getDateNaissance())
                .ageEnMois(a.getAgeEnMois())
                .origine(a.getOrigine().name())
                .statut(a.getStatut().name())
                .poidsActuelKg(a.getPoidsActuelKg())
                .consanguin(a.estConsanguin())
                .especeNom(a.getEspece().getNom())
                .especeIcon(a.getEspece().getIcon())
                .batimentNom(a.getBatiment().getNom())
                .pereNumero(a.getPere() != null ? a.getPere().getNumero() : null)
                .mereNumero(a.getMere() != null ? a.getMere().getNumero() : null)
                .build();
    }

    @Transactional
    public AnimalResponse mettreAJourSexe(UUID animalId, UUID tenantId, String sexe) {
        Animal animal = animalRepository.findByIdAndTenantId(animalId, tenantId)
                .orElseThrow(() -> new RuntimeException("Animal introuvable"));
        animal.setSexe(Animal.SexeEnum.valueOf(sexe));
        animalRepository.save(animal);
        return toResponse(animal);
    }

    public AnimalResponse getById(UUID id, UUID tenantId) {
        Animal animal = animalRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new RuntimeException("Animal introuvable"));
        return toResponse(animal);
    }
}