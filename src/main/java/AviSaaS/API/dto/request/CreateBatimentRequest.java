package AviSaaS.API.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateBatimentRequest {

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @Min(value = 1, message = "Capacité minimum : 1")
    private int capacite;

    private Double surfaceM2;

    private String typeChauffage;

    private String type; // poulailler, porcherie, clapier...

    private UUID fermeId; // fourni par le frontend
}