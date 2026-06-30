package AviSaaS.API.dto.response;

import lombok.*;
import java.util.List;
import java.util.Map;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PonteDashboardResponse {

    // KPIs globaux bande
    private long totalOeufsProduitsTotal;
    private double tauxPonteMoyen;       // moyenne sur les 7 derniers jours
    private double tauxCasseMoyen;
    private double recettesTotales;
    private double coutProductionParOeuf;
    private double margeNette;

    // Stock actuel
    private StockOeufsResponse stockActuel;

    // Ponte du jour (peut être null si pas encore saisie)
    private CollecteResponse ponteAujourdhui;

    // Courbe 30 derniers jours
    private List<PointCourbe> courbe30j;

    // Répartition calibres (%)
    private Map<String, Double> repartitionCalibresPct;

    // Dernières ventes
    private List<VenteResponse> dernieresVentes;

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class PointCourbe {
        private String date;
        private Integer oeufsTotal;   // null si pas de collecte ce jour
        private Double tauxPonte;
    }
}