package AviSaaS.API.service;

import AviSaaS.API.dto.request.CreateStockItemRequest;
import AviSaaS.API.dto.request.MouvementStockRequest;
import AviSaaS.API.dto.response.StockItemResponse;
import AviSaaS.API.entity.*;
import AviSaaS.API.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockItemRepository itemRepository;
    private final MouvementStockRepository mouvementRepository;
    private final TenantRepository tenantRepository;

    @Transactional
    public StockItemResponse creer(CreateStockItemRequest req, UUID tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant introuvable"));

        StockItem.CategorieStock cat;
        try { cat = StockItem.CategorieStock.valueOf(req.getCategorie()); }
        catch (IllegalArgumentException e) {
            throw new RuntimeException("Catégorie invalide : " + req.getCategorie());
        }

        StockItem item = StockItem.builder()
                .tenant(tenant).nom(req.getNom()).categorie(cat)
                .unite(req.getUnite()).quantiteActuelle(req.getQuantiteActuelle())
                .seuilAlerte(req.getSeuilAlerte()).prixUnitaire(req.getPrixUnitaire())
                .fournisseur(req.getFournisseur())
                .build();

        return toResponse(itemRepository.save(item));
    }

    public List<StockItemResponse> lister(UUID tenantId) {
        return itemRepository.findByTenantIdOrderByNomAsc(tenantId)
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public StockItemResponse enregistrerMouvement(UUID itemId, UUID tenantId,
                                                  MouvementStockRequest req) {
        StockItem item = itemRepository.findByIdAndTenantId(itemId, tenantId)
                .orElseThrow(() -> new RuntimeException("Article introuvable"));

        MouvementStock.TypeMouvement type;
        try { type = MouvementStock.TypeMouvement.valueOf(req.getType()); }
        catch (IllegalArgumentException e) {
            throw new RuntimeException("Type de mouvement invalide");
        }

        if (type == MouvementStock.TypeMouvement.SORTIE
                && req.getQuantite() > item.getQuantiteActuelle()) {
            throw new RuntimeException(
                    "Stock insuffisant — disponible : " + item.getQuantiteActuelle());
        }

        MouvementStock mvt = MouvementStock.builder()
                .item(item).type(type).date(req.getDate())
                .quantite(req.getQuantite()).motif(req.getMotif())
                .build();
        mouvementRepository.save(mvt);

        double nouvelleQte = type == MouvementStock.TypeMouvement.ENTREE
                ? item.getQuantiteActuelle() + req.getQuantite()
                : item.getQuantiteActuelle() - req.getQuantite();
        item.setQuantiteActuelle(nouvelleQte);
        itemRepository.save(item);

        return toResponse(item);
    }

    private StockItemResponse toResponse(StockItem i) {
        return StockItemResponse.builder()
                .id(i.getId()).nom(i.getNom())
                .categorie(i.getCategorie().name())
                .unite(i.getUnite()).quantiteActuelle(i.getQuantiteActuelle())
                .seuilAlerte(i.getSeuilAlerte()).prixUnitaire(i.getPrixUnitaire())
                .fournisseur(i.getFournisseur())
                .enAlerte(i.estEnAlerte())
                .build();
    }
}