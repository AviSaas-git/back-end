package AviSaaS.API.dto.request;

import jakarta.validation.constraints.Min;
//import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class CreateBandeRequest {

    @NotNull(message = "L'espèce est obligatoire")
    private UUID especeId;

    @NotNull(message = "Le bâtiment est obligatoire")
    private UUID batimentId;

    @Min(value = 1, message = "L'effectif minimum est 1")
    private int effectifInitial;

    private String race;

    @NotNull(message = "La date d'arrivée est obligatoire")
    private LocalDate dateArrivee;

    private String fournisseur;
}