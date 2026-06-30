package AviSaaS.API.repository;



import AviSaaS.API.entity.StockOeufs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StockOeufsRepository extends JpaRepository<StockOeufs, UUID> {

    // Stock par bande, trié du plus vieux au plus récent (FIFO)
    List<StockOeufs> findByBandeIdAndQuantiteDisponibleGreaterThanOrderByDateCollecteAsc(
            UUID bandeId, int min);

    // Stock d'un calibre précis pour une bande
    List<StockOeufs> findByBandeIdAndCalibreAndQuantiteDisponibleGreaterThanOrderByDateCollecteAsc(
            UUID bandeId, StockOeufs.Calibre calibre, int min);

    // Résumé stock par calibre
    @Query("SELECT s.calibre, SUM(s.quantiteDisponible) FROM StockOeufs s " +
            "WHERE s.tenant.id = :tenantId AND s.quantiteDisponible > 0 " +
            "GROUP BY s.calibre")
    List<Object[]> resumeStockParCalibre(UUID tenantId);

    @Query("SELECT s.calibre, SUM(s.quantiteDisponible) FROM StockOeufs s " +
            "WHERE s.bande.id = :bandeId AND s.quantiteDisponible > 0 " +
            "GROUP BY s.calibre")
    List<Object[]> resumeStockParCalibeBande(UUID bandeId);
}