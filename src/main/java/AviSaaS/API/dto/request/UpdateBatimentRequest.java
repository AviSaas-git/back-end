package AviSaaS.API.dto.request;

import lombok.Data;

@Data
public class UpdateBatimentRequest {
    private String nom;
    private Integer capacite;
    private Double surfaceM2;
    private String typeChauffage;
    private String type;
}