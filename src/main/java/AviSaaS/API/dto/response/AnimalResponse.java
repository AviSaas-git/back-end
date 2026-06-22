package AviSaaS.API.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnimalResponse {
    private UUID      id;
    private String    numero;
    private String    nom;
    private String    sexe;
    private LocalDate dateNaissance;
    private int       ageEnMois;
    private String    origine;
    private String    statut;
    private Double    poidsActuelKg;
    private boolean   consanguin;

    // Espèce
    private String    especeNom;
    private String    especeIcon;

    // Bâtiment
    private String    batimentNom;

    // Généalogie
    private String    pereNumero;
    private String    mereNumero;
}