package AviSaaS.API.dto.response;



import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class MortaliteResponse {
    private UUID id;
    private LocalDate date;
    private int nombreMorts;
    private String cause;
}