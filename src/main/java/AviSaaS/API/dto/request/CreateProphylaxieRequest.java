package AviSaaS.API.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateProphylaxieRequest {
    @NotNull(message = "La date d'application est obligatoire")
    private LocalDate dateApplication;

    @NotBlank(message = "Le nom du traitement est obligatoire")
    private String traitement;

    private String laboratoire;

    @NotBlank(message = "Le dosage est obligatoire")
    private String dosage;

    private String voieAdministration; // EAU_DE_BOISSON, INJECTION, SPRAY, ALIMENT, OCULAIRE_NASALE
    private String observations;
}