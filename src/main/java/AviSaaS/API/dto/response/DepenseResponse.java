package AviSaaS.API.dto.response;
import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class DepenseResponse {
    private UUID id;
    private LocalDate date;
    private String categorie;
    private String sousCategorie;
    private double montant;
    private String fournisseur;
    private String description;
}