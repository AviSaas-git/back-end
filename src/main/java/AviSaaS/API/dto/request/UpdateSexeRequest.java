package AviSaaS.API.dto.request;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateSexeRequest {
    @NotBlank(message = "Le sexe est obligatoire")
    private String sexe; // MALE ou FEMELLE
}
