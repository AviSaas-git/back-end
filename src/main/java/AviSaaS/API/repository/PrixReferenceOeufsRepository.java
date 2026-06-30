package AviSaaS.API.repository;

import AviSaaS.API.entity.PrixReferenceOeufs;
import AviSaaS.API.entity.StockOeufs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PrixReferenceOeufsRepository extends JpaRepository<PrixReferenceOeufs, UUID> {
    List<PrixReferenceOeufs> findByTenantId(UUID tenantId);
    Optional<PrixReferenceOeufs> findByTenantIdAndCalibre(UUID tenantId, StockOeufs.Calibre calibre);
}