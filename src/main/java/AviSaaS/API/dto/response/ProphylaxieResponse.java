package AviSaaS.API.dto.response;
import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ProphylaxieResponse {
    private UUID id;
    private LocalDate dateApplication;
    private int ageEnJoursAuTraitement;
    private String traitement;
    private String laboratoire;
    private String dosage;
    private String voieAdministration;
    private String observations;
}