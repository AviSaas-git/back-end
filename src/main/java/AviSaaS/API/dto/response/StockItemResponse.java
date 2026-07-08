package AviSaaS.API.dto.response;

import lombok.*;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class StockItemResponse {
    private UUID id;
    private String nom;
    private String categorie;
    private String unite;
    private double quantiteActuelle;
    private double seuilAlerte;
    private double prixUnitaire;
    private String fournisseur;
    private boolean enAlerte;
}