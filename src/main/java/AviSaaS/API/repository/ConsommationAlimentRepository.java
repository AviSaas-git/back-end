package AviSaaS.API.repository;

import AviSaaS.API.entity.ConsommationAliment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ConsommationAlimentRepository
        extends JpaRepository<ConsommationAliment, UUID> {

    List<ConsommationAliment> findByBandeIdOrderByDateDesc(UUID bandeId);

    @Query("SELECT SUM(c.coutTotal) FROM ConsommationAliment c " +
            "WHERE c.bande.id = :bandeId")
    Double coutTotalParBande(UUID bandeId);

    @Query("SELECT SUM(c.quantiteKg) FROM ConsommationAliment c " +
            "WHERE c.bande.id = :bandeId")
    Double quantiteTotaleParBande(UUID bandeId);
}