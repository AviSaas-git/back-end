package AviSaaS.API.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FermeResponse {
    private UUID   id;
    private String nom;
    private String localisation;
    private Double surfaceM2;
    private int    capaciteMax;
    private UUID   tenantId;
}