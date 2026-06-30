package AviSaaS.API.dto.response;

import lombok.*;
import java.util.Map;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class StockOeufsResponse {
    private Map<String, StockCalibreDetail> parCalibre;
    private int totalOeufs;
    private int totalAlveoles;
    private int totalCartons;
    private boolean alerteDlc;    // si au moins 1 lot > 15j
    private int nombreLotsExpires;

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class StockCalibreDetail {
        private int quantite;
        private int alveoles;
        private int alveolesPleines;
        private int resteOeufs;
        private int cartons;
        private int ageMoyenJours;
        private boolean alerteDlc;
    }
}