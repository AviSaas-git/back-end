package AviSaaS.API.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateFermeRequest {

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    private String localisation;

    private Double surfaceM2;

    @Min(value = 1, message = "Capacité minimum : 1")
    private int capaciteMax;
}