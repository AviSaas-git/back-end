package AviSaaS.API.dto.response;


import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PeseeResponse {
    private UUID id;
    private LocalDate date;
    private int ageEnJours;
    private int nombreSujetsPeses;
    private double poidsMoyenGrammes;
    private Double poidsCibleGrammes;  // null si pas de référence ce jour-là
    private Double ecartPct;           // (réel - cible) / cible * 100
}