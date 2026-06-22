package AviSaaS.API.dto.response;


import lombok.*;
import java.util.List;
import java.util.Map;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class FinancesBandeResponse {

    private double totalDepenses;
    private double totalProduction;
    private double totalChargeFixe;
    private double coutParOiseau;       // totalDepenses / effectifInitial
    private double pctAlimentation;     // % du total

    private Map<String, Double> parSousCategorie;
    private List<DepenseResponse>    dernieresDepenses;
}