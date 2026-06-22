package AviSaaS.API.repository;

import AviSaaS.API.entity.Bande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BandeRepository extends JpaRepository<Bande, UUID> {

    List<Bande> findByTenantIdOrderByCreatedAtDesc(UUID tenantId);
    Optional<Bande> findByIdAndTenantId(UUID id, UUID tenantId);
    long countByTenantIdAndStatut(UUID tenantId, Bande.StatutBande statut);
    long countByTenantIdAndDateArriveeBetween(UUID tenantId, LocalDate start, LocalDate end);
    long countByTenantId(UUID tenantId);

  //  boolean existsByBatimentIdAndStatut(UUID id, Bande.StatutBande statutBande);

    boolean existsByBatimentIdAndStatut(
            UUID batimentId,
            Bande.StatutBande statut
    );
}