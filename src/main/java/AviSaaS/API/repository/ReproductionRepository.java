package AviSaaS.API.repository;


import AviSaaS.API.entity.Reproduction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReproductionRepository extends JpaRepository<Reproduction, UUID> {

    List<Reproduction> findByTenantIdOrderByDateSaillieDesc(UUID tenantId);

    Optional<Reproduction> findByIdAndTenantId(UUID id, UUID tenantId);

    boolean existsByFemelleIdAndStatutIn(UUID femelleId, List<Reproduction.StatutRepro> statuts);

    List<Reproduction> findByTenantIdAndStatutAndDateMiseBasPrevueBetween(
            UUID tenantId, Reproduction.StatutRepro statut, LocalDate debut, LocalDate fin);
}