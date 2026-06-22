package AviSaaS.API.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class CreateReproductionRequest {
    @NotNull(message = "La femelle est obligatoire")
    private UUID femelleId;

    private UUID maleId; // optionnel

    @NotNull(message = "La date de saillie est obligatoire")
    private LocalDate dateSaillie;

    private String observations;
}
