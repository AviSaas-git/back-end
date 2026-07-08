package AviSaaS.API.repository;

import AviSaaS.API.entity.StockItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StockItemRepository extends JpaRepository<StockItem, UUID> {
    List<StockItem> findByTenantIdOrderByNomAsc(UUID tenantId);
    Optional<StockItem> findByIdAndTenantId(UUID id, UUID tenantId);
}