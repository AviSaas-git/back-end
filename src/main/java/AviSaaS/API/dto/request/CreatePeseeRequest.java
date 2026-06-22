package AviSaaS.API.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreatePeseeRequest {

    @NotNull(message = "La date est obligatoire")
    private LocalDate date;

    @Min(value = 1, message = "Minimum 1 sujet pesé")
    private int nombreSujetsPeses;

    @Min(value = 1, message = "Poids total invalide")
    private double poidsTotalKg;   // ex: 38.4 kg pour 20 oiseaux
}