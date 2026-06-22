package AviSaaS.API.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class EnregistrerPorteeRequest {
    @NotNull(message = "La date de mise bas est obligatoire")
    private LocalDate dateMiseBas;

    @Min(value = 0, message = "Valeur invalide")
    private int nombreNesVivants;

    @Min(value = 0, message = "Valeur invalide")
    private int nombreNesMorts;

    private Double poidsMoyenNaissanceGrammes;
    private String observations;
    private String prefixeNumero; // optionnel — sinon auto depuis l'espèce
}