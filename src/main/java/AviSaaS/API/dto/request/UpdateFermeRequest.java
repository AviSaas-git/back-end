package AviSaaS.API.dto.request;

import lombok.Data;

@Data
public class UpdateFermeRequest {
    private String nom;
    private String localisation;
    private Double surfaceM2;
    private Integer capaciteMax;
}