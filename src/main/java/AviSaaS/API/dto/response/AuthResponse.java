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
public class AuthResponse {

    private String token;

    // Infos user retournées au frontend
    private UUID    userId;
    private String  nom;
    private String  email;
    private String  role;
    private UUID    tenantId;
    private String  plan;
}