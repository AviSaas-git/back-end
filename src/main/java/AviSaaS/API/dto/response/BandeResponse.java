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
public class BandeResponse {
    private UUID      id;
    private String    reference;
    private String    especeNom;
    private String    especeIcon;
    private String    especeMode;    // LOT ou INDIVIDUEL
    private String    batimentNom;
    private String    race;
    private int       effectifInitial;
    private int       effectifActuel;
    private LocalDate dateArrivee;
    private String    statut;
    private double    tauxMortalite;
    private int       ageEnJours;
    private int       progressionPct;
    private UUID especeId;
}