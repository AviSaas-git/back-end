package AviSaaS.API.dto.response;

import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ConsommationResponse {
    private UUID id;
    private LocalDate date;
    private String typeAliment;
    private double quantiteKg;
    private double prixParKgFcfa;
    private double montantFcfa;

    private String observations;
    private String nom;



    private double prixRevientKgMoment;
    private double coutTotal;
}