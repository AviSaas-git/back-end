package AviSaaS.API.dto.request;

import lombok.Data;
import java.util.UUID;

@Data
public class UpdateBandeRequest {
    private String race;
    private String fournisseur;
    private UUID batimentId;
    private String statut;      // ACTIVE ou CLOTUREE
}