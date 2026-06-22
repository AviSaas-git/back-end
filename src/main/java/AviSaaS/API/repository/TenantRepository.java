package AviSaaS.API.repository;

import AviSaaS.API.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, UUID> {
    // Spring génère toutes les requêtes de base automatiquement
    // save(), findById(), findAll(), deleteById()...
}