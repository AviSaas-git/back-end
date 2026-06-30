package AviSaaS.API.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AchatIngredientRequest {
    @NotNull  private LocalDate date;
    @DecimalMin("0.1") private double quantiteKg;
    @DecimalMin("1")   private double prixUnitaireKg;
    private String fournisseur;
    private String observations;
}