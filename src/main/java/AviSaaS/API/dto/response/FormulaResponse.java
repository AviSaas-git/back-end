package AviSaaS.API.dto.response;

import lombok.*;
import java.util.List;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class FormulaResponse {
    private UUID id;
    private String nom;
    private String description;
    private String especeNom;
    private String phase;
    private double prixRevientKg;      // calculé dynamiquement
    private double proportionTotale;   // somme des proportions (doit = ~100)
    private List<LigneFormulaResponse> composition;

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class LigneFormulaResponse {
        private UUID   ingredientId;
        private String ingredientNom;
        private String ingredientUnite;
        private double proportionPour100kg;
        private double prixIngredientKg;
        private double coutContribution;  // proportionPour100kg/100 * prixKg * 100
        private String observations;
    }
}