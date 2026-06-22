package AviSaaS.API.repository;


import AviSaaS.API.entity.SuiviMortalite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SuiviMortaliteRepository extends JpaRepository<SuiviMortalite, UUID> {
    List<SuiviMortalite> findByBandeIdOrderByDateDesc(UUID bandeId);
}