package AviSaaS.API.dto.response;
import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class EvenementAnimalResponse {
    private UUID id;
    private LocalDate date;
    private String type;
    private Double poidsKg;
    private String traitement;
    private String laboratoire;
    private String dosage;
    private String voieAdministration;
    private String cause;
    private String observations;
}