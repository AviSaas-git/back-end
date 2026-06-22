package AviSaaS.API.dto.response;



import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ReproductionResponse {
    private UUID id;
    private String femelleNumero;
    private String femelleNom;
    private String maleNumero;
    private String especeNom;
    private String especeIcon;
    private LocalDate dateSaillie;
    private LocalDate dateMiseBasPrevue;
    private int joursRestants;
    private String statut;
    private String observations;
    private boolean porteeEnregistree;
}