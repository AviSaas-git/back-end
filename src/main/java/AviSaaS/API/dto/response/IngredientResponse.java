package AviSaaS.API.dto.response;

import lombok.*;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class IngredientResponse {
    private UUID id;
    private String nom;
    private String unite;
    private double prixUnitaireKg;
    private String fournisseur;
    private String description;
}