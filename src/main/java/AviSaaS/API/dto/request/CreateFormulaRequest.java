package AviSaaS.API.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class CreateFormulaRequest {
    @NotBlank private String nom;
    private String description;
    private UUID   especeId;
    private String phase; // DEMARRAGE, CROISSANCE, FINITION, UNIQUE...

    @NotEmpty(message = "La formule doit contenir au moins un ingrédient")
    private List<LigneFormule> composition;

    @Data
    public static class LigneFormule {
        private UUID   ingredientId;
        private double proportionPour100kg;
        private String observations;
    }
}