package AviSaaS.API.dto.response;

import lombok.*;
import java.util.List;
import java.util.Map;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AlimentationBandeResponse {
    private double totalKgConsommes;
    private double coutTotalAlimentation;
    private double coutParKgVifMoyen;    // coût aliment / (poids moyen actuel * effectif)
    private double consommationParOiseauKg; // kg total / effectif initial
    private Map<String, Double>  coutParFormule;
    private List<ConsommationResponse> historique;
}