package AviSaaS.API.repository;

import AviSaaS.API.entity.CollectePonte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CollectePonteRepository extends JpaRepository<CollectePonte, UUID> {

    List<CollectePonte> findByBandeIdOrderByDateDesc(UUID bandeId);

    Optional<CollectePonte> findByBandeIdAndDate(UUID bandeId, LocalDate date);

    @Query("SELECT SUM(c.oeufsPetit + c.oeufsMovyen + c.oeufsGros + c.oeufsSupGros) " +
            "FROM CollectePonte c WHERE c.bande.id = :bandeId")
    Long totalCommercialisablesParBande(UUID bandeId);

    @Query("SELECT SUM(c.oeufsPetit + c.oeufsMovyen + c.oeufsGros + c.oeufsSupGros) " +
            "FROM CollectePonte c WHERE c.bande.id = :bandeId " +
            "AND c.date BETWEEN :debut AND :fin")
    Long totalParPeriode(UUID bandeId, LocalDate debut, LocalDate fin);

    List<CollectePonte> findByBandeIdAndDateBetweenOrderByDateDesc(
            UUID bandeId, LocalDate debut, LocalDate fin);
}