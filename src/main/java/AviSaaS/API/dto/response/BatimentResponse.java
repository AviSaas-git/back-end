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
public class BatimentResponse {
    private UUID   id;
    private String nom;
    private int    capacite;
    private Double surfaceM2;
    private String typeChauffage;
    private String type;
    private UUID   fermeId;
}