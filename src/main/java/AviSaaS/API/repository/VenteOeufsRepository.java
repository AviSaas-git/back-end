package AviSaaS.API.repository;


import AviSaaS.API.entity.VenteOeufs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VenteOeufsRepository extends JpaRepository<VenteOeufs, UUID> {

    List<VenteOeufs> findByTenantIdOrderByDateDesc(UUID tenantId);

    List<VenteOeufs> findByBandeIdOrderByDateDesc(UUID bandeId);

    Optional<VenteOeufs> findByIdAndTenantId(UUID id, UUID tenantId);

    @Query("SELECT SUM(v.montantTotal) FROM VenteOeufs v " +
            "WHERE v.tenant.id = :tenantId AND v.date BETWEEN :debut AND :fin")
    Double totalVentesParPeriode(UUID tenantId, LocalDate debut, LocalDate fin);

    @Query("SELECT SUM(v.montantTotal) FROM VenteOeufs v WHERE v.bande.id = :bandeId")
    Double totalVentesParBande(UUID bandeId);
}