package AviSaaS.API.service;



import AviSaaS.API.dto.request.CreateDepenseRequest;
import AviSaaS.API.dto.response.DepenseResponse;
import AviSaaS.API.dto.response.FinancesBandeResponse;
import AviSaaS.API.entity.Bande;
import AviSaaS.API.entity.DepenseBande;
import AviSaaS.API.repository.BandeRepository;
import AviSaaS.API.repository.DepenseBandeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FinancesService {

    private final DepenseBandeRepository depenseRepository;
    private final BandeRepository        bandeRepository;

    @Transactional
    public DepenseResponse ajouterDepense(UUID bandeId, UUID tenantId,
                                          CreateDepenseRequest req) {

        Bande bande = bandeRepository.findByIdAndTenantId(bandeId, tenantId)
                .orElseThrow(() -> new RuntimeException("Bande introuvable"));

        DepenseBande.CategorieDepense categorie;
        try {
            categorie = DepenseBande.CategorieDepense.valueOf(req.getCategorie());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Catégorie invalide : " + req.getCategorie());
        }

        DepenseBande depense = DepenseBande.builder()
                .bande(bande)
                .date(req.getDate())
                .categorie(categorie)
                .sousCategorie(req.getSousCategorie())
                .montant(req.getMontant())
                .fournisseur(req.getFournisseur())
                .description(req.getDescription())
                .build();

        depense = depenseRepository.save(depense);
        return toResponse(depense);
    }

    public FinancesBandeResponse getFinances(UUID bandeId, UUID tenantId) {

        Bande bande = bandeRepository.findByIdAndTenantId(bandeId, tenantId)
                .orElseThrow(() -> new RuntimeException("Bande introuvable"));

        double total = nvl(depenseRepository.totalParBande(bandeId));
        double totalProd = nvl(depenseRepository.totalParCategorie(
                bandeId, DepenseBande.CategorieDepense.PRODUCTION));
        double totalFix  = nvl(depenseRepository.totalParCategorie(
                bandeId, DepenseBande.CategorieDepense.CHARGE_FIXE));

        // Totaux par sous-catégorie pour le graphique
        Map<String, Double> parSousCategorie = new LinkedHashMap<>();
        for (Object[] row : depenseRepository.totalParSousCategorie(bandeId)) {
            parSousCategorie.put((String) row[0], (Double) row[1]);
        }

        // % alimentation
        double pctAlim = total > 0
                ? parSousCategorie.getOrDefault("Alimentation", 0.0) / total * 100
                : 0;

        double coutParOiseau = bande.getEffectifInitial() > 0
                ? total / bande.getEffectifInitial() : 0;

        List<DepenseResponse> dernieres = depenseRepository
                .findByBandeIdOrderByDateDesc(bandeId)
                .stream().map(this::toResponse).toList();

        return FinancesBandeResponse.builder()
                .totalDepenses(total)
                .totalProduction(totalProd)
                .totalChargeFixe(totalFix)
                .coutParOiseau(coutParOiseau)
                .pctAlimentation(pctAlim)
                .parSousCategorie(parSousCategorie)
                .dernieresDepenses(dernieres)
                .build();
    }

    private DepenseResponse toResponse(DepenseBande d) {
        return DepenseResponse.builder()
                .id(d.getId())
                .date(d.getDate())
                .categorie(d.getCategorie().name())
                .sousCategorie(d.getSousCategorie())
                .montant(d.getMontant())
                .fournisseur(d.getFournisseur())
                .description(d.getDescription())
                .build();
    }

    private double nvl(Double v) { return v != null ? v : 0.0; }


    @Transactional
    public DepenseResponse modifier(UUID id, UUID tenantId, CreateDepenseRequest req) {
        DepenseBande dep = depenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dépense introuvable"));
        if (!dep.getBande().getTenant().getId().equals(tenantId))
            throw new RuntimeException("Accès non autorisé");

        if (req.getDate()          != null) dep.setDate(req.getDate());
        if (req.getMontant()       > 0)     dep.setMontant(req.getMontant());
        if (req.getSousCategorie() != null) dep.setSousCategorie(req.getSousCategorie());
        if (req.getDescription()   != null) dep.setDescription(req.getDescription());

        return toResponse(depenseRepository.save(dep));
    }

    @Transactional
    public void supprimer(UUID id, UUID tenantId) {
        DepenseBande dep = depenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dépense introuvable"));
        if (!dep.getBande().getTenant().getId().equals(tenantId))
            throw new RuntimeException("Accès non autorisé");
        depenseRepository.delete(dep);
    }
}