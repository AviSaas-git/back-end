package AviSaaS.API.dto.response;
import lombok.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class VenteResponse {
    private UUID id;
    private LocalDate date;
    private String client;
    private String telephone;
    private double montantTotal;
    private List<LigneResponse> lignes;
    private String observations;

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class LigneResponse {
        private String calibre;
        private int nombreOeufs;
        private int nombreAlveoles;
        private double prixAlveole;
        private double montantLigne;
    }
}