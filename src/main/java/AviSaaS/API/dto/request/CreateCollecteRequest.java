package AviSaaS.API.dto.request;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateCollecteRequest {
    @NotNull private LocalDate date;
    @Min(0)  private int oeufsPetit;
    @Min(0)  private int oeufsMovyen;
    @Min(0)  private int oeufsGros;
    @Min(0)  private int oeufsSupGros;
    @Min(0)  private int oeufsCasses;
    @Min(0)  private int oeufsDeclasses;
    private String observations;
}