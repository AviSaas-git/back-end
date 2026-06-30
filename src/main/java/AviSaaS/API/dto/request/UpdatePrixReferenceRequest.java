package AviSaaS.API.dto.request;


import lombok.Data;
import java.util.Map;

@Data
public class UpdatePrixReferenceRequest {
    // calibre (PETIT/MOYEN/GROS/SUPER_GROS) → prixAlveole
    private Map<String, Double> prix;
}