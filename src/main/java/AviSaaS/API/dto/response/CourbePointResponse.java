package AviSaaS.API.dto.response;


import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CourbePointResponse {
    private int jour;
    private Double poidsCibleGrammes;  // null = pas de donnée fournisseur ce jour
    private Double poidsReelGrammes;   // null = pas de pesée ce jour
}