package AviSaaS.API.dto.response;

import lombok.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PorteeResponse {
    private UUID id;
    private LocalDate dateMiseBas;
    private int nombreNesTotal;
    private int nombreNesVivants;
    private int nombreNesMorts;
    private Double poidsMoyenNaissanceGrammes;
    private List<String> numerosAnimauxGeneres;
}