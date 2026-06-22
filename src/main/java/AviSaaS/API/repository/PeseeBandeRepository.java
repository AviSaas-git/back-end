package AviSaaS.API.repository;
import AviSaaS.API.entity.PeseeBande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PeseeBandeRepository extends JpaRepository<PeseeBande, UUID> {
    List<PeseeBande> findByBandeIdOrderByDateAsc(UUID bandeId);
}