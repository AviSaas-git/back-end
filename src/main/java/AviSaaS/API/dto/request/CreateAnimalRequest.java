package AviSaaS.API.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class CreateAnimalRequest {

    @NotNull(message = "L'espèce est obligatoire")
    private UUID especeId;

    @NotNull(message = "Le bâtiment est obligatoire")
    private UUID batimentId;

    @NotBlank(message = "Le numéro est obligatoire")
    private String numero;

    private String nom;             // optionnel

    @NotNull(message = "Le sexe est obligatoire")
    private String sexe;            // MALE ou FEMELLE

    @NotNull(message = "La date de naissance est obligatoire")
    private LocalDate dateNaissance;

    @NotNull(message = "L'origine est obligatoire")
    private String origine;         // ACHAT ou NAISSANCE_FERME

    private Double poidsActuelKg;

    private UUID pereId;            // optionnel
    private UUID mereId;            // optionnel
}