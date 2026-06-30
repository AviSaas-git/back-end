package AviSaaS.API.service;

import AviSaaS.API.dto.request.CreateEvenementAnimalRequest;
import AviSaaS.API.dto.response.EvenementAnimalResponse;
import AviSaaS.API.entity.*;
import AviSaaS.API.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EvenementAnimalService {

    private final EvenementAnimalRepository evenementRepository;
    private final AnimalRepository          animalRepository;

    @Transactional
    public EvenementAnimalResponse ajouter(UUID animalId, UUID tenantId,
                                           CreateEvenementAnimalRequest req) {

        Animal animal = animalRepository.findByIdAndTenantId(animalId, tenantId)
                .orElseThrow(() -> new RuntimeException("Animal introuvable"));

        EvenementAnimal.TypeEvenement type;
        try {
            type = EvenementAnimal.TypeEvenement.valueOf(req.getType());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Type d'événement invalide : " + req.getType());
        }

        // Si mort → on met à jour le statut de l'animal
        if (type == EvenementAnimal.TypeEvenement.MORT) {
            if (animal.getStatut() == Animal.StatutAnimal.MORT) {
                throw new RuntimeException("Cet animal est déjà déclaré mort");
            }
            animal.setStatut(Animal.StatutAnimal.MORT);
            animalRepository.save(animal);
        }

        // Si vendu → statut VENDU
        if (type == EvenementAnimal.TypeEvenement.VENTE) {
            animal.setStatut(Animal.StatutAnimal.VENDU);
            animalRepository.save(animal);
        }

        // Si pesée → on met à jour le poids actuel
        if (type == EvenementAnimal.TypeEvenement.PESEE && req.getPoidsKg() != null) {
            animal.setPoidsActuelKg(req.getPoidsKg());
            animalRepository.save(animal);
        }

        EvenementAnimal evenement = EvenementAnimal.builder()
                .animal(animal)
                .date(req.getDate())
                .type(type)
                .poidsKg(req.getPoidsKg())
                .traitement(req.getTraitement())
                .laboratoire(req.getLaboratoire())
                .dosage(req.getDosage())
                .voieAdministration(req.getVoieAdministration())
                .cause(req.getCause())
                .observations(req.getObservations())
                .build();

        return toResponse(evenementRepository.save(evenement));
    }

    public List<EvenementAnimalResponse> lister(UUID animalId, UUID tenantId) {
        animalRepository.findByIdAndTenantId(animalId, tenantId)
                .orElseThrow(() -> new RuntimeException("Animal introuvable"));
        return evenementRepository.findByAnimalIdOrderByDateDesc(animalId)
                .stream().map(this::toResponse).toList();
    }

    private EvenementAnimalResponse toResponse(EvenementAnimal e) {
        return EvenementAnimalResponse.builder()
                .id(e.getId())
                .date(e.getDate())
                .type(e.getType().name())
                .poidsKg(e.getPoidsKg())
                .traitement(e.getTraitement())
                .laboratoire(e.getLaboratoire())
                .dosage(e.getDosage())
                .voieAdministration(e.getVoieAdministration())
                .cause(e.getCause())
                .observations(e.getObservations())
                .build();
    }
}