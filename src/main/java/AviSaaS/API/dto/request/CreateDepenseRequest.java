package AviSaaS.API.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateDepenseRequest {

    @NotNull(message = "La date est obligatoire")
    private LocalDate date;

    @NotNull(message = "La catégorie est obligatoire")
    private String categorie; // PRODUCTION ou CHARGE_FIXE

    @NotBlank(message = "La sous-catégorie est obligatoire")
    private String sousCategorie;

    @DecimalMin(value = "1", message = "Montant minimum : 1 FCFA")
    private double montant;

    private String fournisseur;
    private String description;
}