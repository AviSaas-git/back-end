package AviSaaS.API.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateStockItemRequest {
    @NotBlank private String nom;
    @NotBlank private String categorie;
    private String unite;
    private double quantiteActuelle;
    private double seuilAlerte;
    private double prixUnitaire;
    private String fournisseur;
}