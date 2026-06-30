package AviSaaS.API.dto.request;



import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateIngredientRequest {
    @NotBlank(message = "Le nom est obligatoire")
    private String nom;
    private String unite;
    @DecimalMin("1") private double prixUnitaireKg;
    private String fournisseur;
    private String description;
}