package AviSaaS.API.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class MouvementStockRequest {
    @NotNull private LocalDate date;
    @NotNull private String type; // ENTREE ou SORTIE
    @DecimalMin("0.01") private double quantite;
    private String motif;
}