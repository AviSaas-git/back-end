package AviSaaS.API.repository;


import AviSaaS.API.entity.PoidsReference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PoidsReferenceRepository extends JpaRepository<PoidsReference, UUID> {

    List<PoidsReference> findByEspeceIdOrderByJourAsc(UUID especeId);

    // Le point de référence le plus proche, à ce jour ou avant
    Optional<PoidsReference> findFirstByEspeceIdAndJourLessThanEqualOrderByJourDesc(
            UUID especeId, int jour);

    void deleteByEspeceId(UUID especeId);
}