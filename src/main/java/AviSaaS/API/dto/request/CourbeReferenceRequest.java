package AviSaaS.API.dto.request;


import lombok.Data;
import java.util.List;

@Data
public class CourbeReferenceRequest {
    private List<PointReference> points;

    @Data
    public static class PointReference {
        private int jour;
        private double poidsGrammesCible;
    }
}