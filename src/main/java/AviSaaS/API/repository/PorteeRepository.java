package AviSaaS.API.repository;


import AviSaaS.API.entity.Portee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PorteeRepository extends JpaRepository<Portee, UUID> {
}
