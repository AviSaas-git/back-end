package AviSaaS.API.repository;

import AviSaaS.API.entity.Ferme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FermeRepository extends JpaRepository<Ferme, UUID> {

    // Toutes les fermes d'un tenant
    List<Ferme> findByTenantId(UUID tenantId);

    // Une ferme par id ET tenant — sécurité multi-tenant
    Optional<Ferme> findByIdAndTenantId(UUID id, UUID tenantId);
}