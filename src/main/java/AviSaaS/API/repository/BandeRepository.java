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

    // ==========================
    // Recherche
    // ==========================

    List<Bande> findByTenantId(UUID tenantId);

    List<Bande> findByTenantIdOrderByCreatedAtDesc(UUID tenantId);

    Optional<Bande> findByIdAndTenantId(UUID id, UUID tenantId);

    List<Bande> findByBatimentId(UUID batimentId);

    List<Bande> findByBatimentIdAndTenantId(UUID batimentId, UUID tenantId);

    List<Bande> findByStatut(Bande.StatutBande statut);

    List<Bande> findByTenantIdAndStatut(
            UUID tenantId,
            Bande.StatutBande statut
    );

    // ==========================
    // Comptages
    // ==========================

    long countByTenantId(UUID tenantId);

    long countByTenantIdAndStatut(
            UUID tenantId,
            Bande.StatutBande statut
    );

    long countByBatimentIdAndStatut(
            UUID batimentId,
            Bande.StatutBande statut
    );

    long countByBatimentIdAndTenantIdAndStatut(
            UUID batimentId,
            UUID tenantId,
            Bande.StatutBande statut
    );

    // ✅ AJOUTER CETTE METHODE
    long countByTenantIdAndDateArriveeBetween(
            UUID tenantId,
            LocalDate debut,
            LocalDate fin
    );

    // ==========================
    // Existence
    // ==========================

    boolean existsByBatimentIdAndStatut(
            UUID batimentId,
            Bande.StatutBande statut
    );

    boolean existsByBatimentIdAndTenantIdAndStatut(
            UUID batimentId,
            UUID tenantId,
            Bande.StatutBande statut
    );
}