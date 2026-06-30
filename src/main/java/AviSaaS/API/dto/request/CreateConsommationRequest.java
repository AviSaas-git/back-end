package AviSaaS.API.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class CreateConsommationRequest {

    @NotNull(message = "La date est obligatoire")
    private LocalDate date;

    @NotNull(message = "La formule est obligatoire")
    private UUID formulaId;

    @DecimalMin(value = "0.1", message = "Quantité minimum : 0.1 kg")
    private double quantiteKg;

    private String observations;
}