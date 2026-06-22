package AviSaaS.API.repository;

import AviSaaS.API.entity.Batiment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BatimentRepository extends JpaRepository<Batiment, UUID> {

    // Tous les bâtiments d'une ferme
    List<Batiment> findByFermeId(UUID fermeId);

    // Tous les bâtiments d'un tenant via la ferme
    List<Batiment> findByFermeTenantId(UUID tenantId);
    // Ajoute cette méthode — sécurité multi-tenant
    Optional<Batiment> findByIdAndFermeTenantId(UUID id, UUID tenantId);
}