package AviSaaS.API.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {

    // KPIs
    private long   totalBandesActives;
    private long   totalAnimauxIndividuels;
    private long   totalFermes;
    private long   totalBatiments;

    // Listes
    private List<FermeResponse>    fermes;
    private List<BatimentResponse> batiments;
}