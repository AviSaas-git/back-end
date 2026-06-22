package AviSaaS.API.repository;

import AviSaaS.API.entity.DepenseBande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DepenseBandeRepository extends JpaRepository<DepenseBande, UUID> {

    List<DepenseBande> findByBandeIdOrderByDateDesc(UUID bandeId);

    @Query("SELECT SUM(d.montant) FROM DepenseBande d WHERE d.bande.id = :bandeId")
    Double totalParBande(UUID bandeId);

    @Query("SELECT SUM(d.montant) FROM DepenseBande d " +
            "WHERE d.bande.id = :bandeId AND d.categorie = :categorie")
    Double totalParCategorie(UUID bandeId,
                             DepenseBande.CategorieDepense categorie);

    @Query("SELECT d.sousCategorie, SUM(d.montant) FROM DepenseBande d " +
            "WHERE d.bande.id = :bandeId GROUP BY d.sousCategorie " +
            "ORDER BY SUM(d.montant) DESC")
    List<Object[]> totalParSousCategorie(UUID bandeId);
}