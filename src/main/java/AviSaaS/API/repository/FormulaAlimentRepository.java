package AviSaaS.API.repository;

import AviSaaS.API.entity.FormulaAliment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FormulaAlimentRepository extends JpaRepository<FormulaAliment, UUID> {
    List<FormulaAliment> findByTenantIdAndActifTrueOrderByNomAsc(UUID tenantId);
    Optional<FormulaAliment> findByIdAndTenantId(UUID id, UUID tenantId);
}