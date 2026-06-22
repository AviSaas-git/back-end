package AviSaaS.API.repository;

import AviSaaS.API.entity.Animal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AnimalRepository extends JpaRepository<Animal, UUID> {

    List<Animal> findByTenantIdOrderByCreatedAtDesc(UUID tenantId);
    Optional<Animal> findByIdAndTenantId(UUID id, UUID tenantId);
    List<Animal> findByTenantIdAndEspeceIdAndSexe(
            UUID tenantId, UUID especeId, Animal.SexeEnum sexe);

    boolean existsByNumeroAndTenantId(String numero, UUID tenantId);

    long countByTenantIdAndStatut(UUID tenantId, Animal.StatutAnimal statut);
    long countByTenantIdAndNumeroStartingWith(UUID tenantId, String prefix);

}