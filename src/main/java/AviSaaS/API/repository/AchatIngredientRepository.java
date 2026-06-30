package AviSaaS.API.repository;



import AviSaaS.API.entity.AchatIngredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AchatIngredientRepository extends JpaRepository<AchatIngredient, UUID> {
    List<AchatIngredient> findByIngredientIdOrderByDateDesc(UUID ingredientId);
}