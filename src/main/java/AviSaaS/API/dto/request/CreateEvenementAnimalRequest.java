package AviSaaS.API.dto.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateEvenementAnimalRequest {
    @NotNull  private LocalDate date;
    @NotBlank private String type; // PESEE, PROPHYLAXIE, MALADIE, MORT, VENTE, AUTRE

    // Pesée
    private Double poidsKg;

    // Prophylaxie
    private String traitement;
    private String laboratoire;
    private String dosage;
    private String voieAdministration;

    // Cause (mortalité, maladie...)
    private String cause;

    private String observations;
}