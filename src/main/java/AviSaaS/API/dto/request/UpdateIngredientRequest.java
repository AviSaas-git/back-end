package AviSaaS.API.dto.request;

import lombok.Data;

@Data
public class UpdateIngredientRequest {
    private String nom;
    private String unite;
    private Double prixUnitaireKg;
    private String fournisseur;
    private String description;
    private Boolean actif;
}