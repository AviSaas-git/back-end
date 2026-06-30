package AviSaaS.API.dto.response;


import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CollecteResponse {
    private UUID id;
    private LocalDate date;
    private int ageEnJours;
    private int oeufsPetit;
    private int oeufsMovyen;
    private int oeufsGros;
    private int oeufsSupGros;
    private int oeufsCasses;
    private int oeufsDeclasses;
    private int totalCommercialisables;
    private int totalCollectes;
    private double tauxPonte;
    private double tauxCasse;
    // Conditionnement calculé par calibre
    private ConditionnementResponse conditionnement;
    private String observations;
}