package AviSaaS.API.dto.request;

import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class UpdateAnimalRequest {
    private String    nom;
    private String    sexe;        // MALE, FEMELLE, INDETERMINE
    private String    statut;      // ACTIF, VENDU, MORT, REPRODUCTEUR
    private Double    poidsActuelKg;
    private UUID      batimentId;
    private LocalDate dateNaissance;
}