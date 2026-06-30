package AviSaaS.API.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CreateVenteRequest {
    @NotNull private LocalDate date;
    private String client;
    private String telephone;
    private String observations;

    @NotEmpty(message = "La vente doit contenir au moins une ligne")
    private List<LigneVente> lignes;

    @Data
    public static class LigneVente {
        private String calibre; // PETIT, MOYEN, GROS, SUPER_GROS
        private int nombreOeufs;
        private double prixAlveole;
    }
}