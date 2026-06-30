package AviSaaS.API.repository;


import AviSaaS.API.entity.EvenementAnimal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EvenementAnimalRepository extends JpaRepository<EvenementAnimal, UUID> {
    List<EvenementAnimal> findByAnimalIdOrderByDateDesc(UUID animalId);
    List<EvenementAnimal> findByAnimalIdAndTypeOrderByDateAsc(
            UUID animalId, EvenementAnimal.TypeEvenement type);
}