package AviSaaS.API.repository;

import AviSaaS.API.entity.SuiviProphylaxie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SuiviProphylaxieRepository extends JpaRepository<SuiviProphylaxie, UUID> {
    List<SuiviProphylaxie> findByBandeIdOrderByDateApplicationAsc(UUID bandeId);
}