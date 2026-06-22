package AviSaaS.API.repository;

import AviSaaS.API.entity.EspeceReference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EspeceReferenceRepository
        extends JpaRepository<EspeceReference, UUID> {
}