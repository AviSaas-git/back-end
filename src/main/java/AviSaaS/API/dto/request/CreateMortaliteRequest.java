package AviSaaS.API.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateMortaliteRequest {
    @NotNull(message = "La date est obligatoire")
    private LocalDate date;

    @Min(value = 1, message = "Minimum 1 mort")
    private int nombreMorts;

    private String cause;
}