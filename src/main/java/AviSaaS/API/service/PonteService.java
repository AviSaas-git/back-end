package AviSaaS.API.service;


import AviSaaS.API.dto.request.CreateCollecteRequest;
import AviSaaS.API.dto.request.CreateVenteRequest;
import AviSaaS.API.dto.request.UpdatePrixReferenceRequest;
import AviSaaS.API.dto.response.*;
import AviSaaS.API.entity.*;
import AviSaaS.API.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PonteService {

    private final CollectePonteRepository  collecteRepository;
    private final StockOeufsRepository     stockRepository;
    private final VenteOeufsRepository     venteRepository;
    private final PrixReferenceOeufsRepository prixRepository;
    private final BandeRepository          bandeRepository;
    private final TenantRepository         tenantRepository;

    // ─── COLLECTE QUOTIDIENNE ─────────────────────────────────────────

    @Transactional
    public CollecteResponse enregistrerCollecte(UUID bandeId, UUID tenantId,
                                                CreateCollecteRequest req) {

        Bande bande = bandeRepository.findByIdAndTenantId(bandeId, tenantId)
                .orElseThrow(() -> new RuntimeException("Bande introuvable"));

        // Empêche 2 collectes le même jour pour la même bande
        if (collecteRepository.findByBandeIdAndDate(bandeId, req.getDate()).isPresent()) {
            throw new RuntimeException(
                    "Une collecte a déjà été saisie le " + req.getDate() + " pour cette bande");
        }

        CollectePonte collecte = CollectePonte.builder()
                .bande(bande)
                .date(req.getDate())
                .oeufsPetit(req.getOeufsPetit())
                .oeufsMovyen(req.getOeufsMovyen())
                .oeufsGros(req.getOeufsGros())
                .oeufsSupGros(req.getOeufsSupGros())
                .oeufsCasses(req.getOeufsCasses())
                .oeufsDeclasses(req.getOeufsDeclasses())
                .observations(req.getObservations())
                .build();

        collecte = collecteRepository.save(collecte);

        // ── Mise à jour stock par calibre ──
        Tenant tenant = tenantRepository.findById(tenantId).orElseThrow();
        mettreAJourStock(bande, tenant, req.getDate(),
                StockOeufs.Calibre.PETIT,     req.getOeufsPetit());
        mettreAJourStock(bande, tenant, req.getDate(),
                StockOeufs.Calibre.MOYEN,     req.getOeufsMovyen());
        mettreAJourStock(bande, tenant, req.getDate(),
                StockOeufs.Calibre.GROS,      req.getOeufsGros());
        mettreAJourStock(bande, tenant, req.getDate(),
                StockOeufs.Calibre.SUPER_GROS,req.getOeufsSupGros());

        return toCollecteResponse(collecte);
    }

    private void mettreAJourStock(Bande bande, Tenant tenant,
                                  LocalDate date, StockOeufs.Calibre calibre, int quantite) {
        if (quantite <= 0) return;
        StockOeufs stock = StockOeufs.builder()
                .bande(bande)
                .tenant(tenant)
                .calibre(calibre)
                .dateCollecte(date)
                .quantiteDisponible(quantite)
                .build();
        stockRepository.save(stock);
    }

    public List<CollecteResponse> listerCollectes(UUID bandeId, UUID tenantId) {
        bandeRepository.findByIdAndTenantId(bandeId, tenantId)
                .orElseThrow(() -> new RuntimeException("Bande introuvable"));
        return collecteRepository.findByBandeIdOrderByDateDesc(bandeId)
                .stream().map(this::toCollecteResponse).toList();
    }

    // ─── VENTES ───────────────────────────────────────────────────────

    @Transactional
    public VenteResponse enregistrerVente(UUID bandeId, UUID tenantId,
                                          CreateVenteRequest req) {

        Bande bande = bandeRepository.findByIdAndTenantId(bandeId, tenantId)
                .orElseThrow(() -> new RuntimeException("Bande introuvable"));
        Tenant tenant = tenantRepository.findById(tenantId).orElseThrow();

        VenteOeufs vente = VenteOeufs.builder()
                .tenant(tenant)
                .bande(bande)
                .date(req.getDate())
                .client(req.getClient())
                .telephone(req.getTelephone())
                .observations(req.getObservations())
                .montantTotal(0)
                .build();

        double montantTotal = 0;

        for (CreateVenteRequest.LigneVente ligne : req.getLignes()) {
            StockOeufs.Calibre calibre =
                    StockOeufs.Calibre.valueOf(ligne.getCalibre());

            // Vérification stock disponible
            int stockDispo = getStockCalibre(bandeId, calibre);
            if (ligne.getNombreOeufs() > stockDispo) {
                throw new RuntimeException(
                        "Stock insuffisant pour le calibre " + calibre +
                                " — disponible : " + stockDispo + " œufs");
            }

            // Déduction FIFO du stock
            deduireStock(bandeId, calibre, ligne.getNombreOeufs());

            // Calcul montant ligne (en alvéoles de 30)
            double alveoles = ligne.getNombreOeufs() / 30.0;
            double montantLigne = alveoles * ligne.getPrixAlveole();
            montantTotal += montantLigne;

            VenteLigneOeufs venteLigne = VenteLigneOeufs.builder()
                    .vente(vente)
                    .calibre(calibre)
                    .nombreOeufs(ligne.getNombreOeufs())
                    .prixAlveole(ligne.getPrixAlveole())
                    .montantLigne(montantLigne)
                    .build();
            vente.getLignes().add(venteLigne);
        }

        vente.setMontantTotal(montantTotal);
        vente = venteRepository.save(vente);
        return toVenteResponse(vente);
    }

    private int getStockCalibre(UUID bandeId, StockOeufs.Calibre calibre) {
        return stockRepository
                .findByBandeIdAndCalibreAndQuantiteDisponibleGreaterThanOrderByDateCollecteAsc(
                        bandeId, calibre, 0)
                .stream().mapToInt(StockOeufs::getQuantiteDisponible).sum();
    }

    private void deduireStock(UUID bandeId, StockOeufs.Calibre calibre, int quantite) {
        List<StockOeufs> lots = stockRepository
                .findByBandeIdAndCalibreAndQuantiteDisponibleGreaterThanOrderByDateCollecteAsc(
                        bandeId, calibre, 0);

        int aDeduire = quantite;
        for (StockOeufs lot : lots) {
            if (aDeduire <= 0) break;
            int pris = Math.min(lot.getQuantiteDisponible(), aDeduire);
            lot.setQuantiteDisponible(lot.getQuantiteDisponible() - pris);
            stockRepository.save(lot);
            aDeduire -= pris;
        }
    }

    public List<VenteResponse> listerVentes(UUID bandeId, UUID tenantId) {
        bandeRepository.findByIdAndTenantId(bandeId, tenantId)
                .orElseThrow(() -> new RuntimeException("Bande introuvable"));
        return venteRepository.findByBandeIdOrderByDateDesc(bandeId)
                .stream().map(this::toVenteResponse).toList();
    }

    // ─── STOCK ───────────────────────────────────────────────────────

    public StockOeufsResponse getStock(UUID bandeId, UUID tenantId) {
        bandeRepository.findByIdAndTenantId(bandeId, tenantId)
                .orElseThrow(() -> new RuntimeException("Bande introuvable"));

        List<StockOeufs> lots = stockRepository
                .findByBandeIdAndQuantiteDisponibleGreaterThanOrderByDateCollecteAsc(bandeId, 0);

        Map<String, StockOeufsResponse.StockCalibreDetail> parCalibre = new LinkedHashMap<>();
        int totalOeufs = 0; boolean alerteDlc = false; int lotsExpires = 0;

        for (StockOeufs.Calibre calibre : StockOeufs.Calibre.values()) {
            List<StockOeufs> lotsCalib = lots.stream()
                    .filter(l -> l.getCalibre() == calibre).toList();
            int qte = lotsCalib.stream().mapToInt(StockOeufs::getQuantiteDisponible).sum();
            if (qte == 0) continue;

            int ageMoyen = lotsCalib.isEmpty() ? 0 :
                    (int) lotsCalib.stream().mapToInt(StockOeufs::getAgeLot).average().orElse(0);
            boolean alert = lotsCalib.stream().anyMatch(StockOeufs::estEnAlerte);
            lotsExpires += lotsCalib.stream().filter(StockOeufs::estExpire)
                    .mapToInt(StockOeufs::getQuantiteDisponible).sum();
            if (alert) alerteDlc = true;

            parCalibre.put(calibre.name(), StockOeufsResponse.StockCalibreDetail.builder()
                    .quantite(qte).alveoles(qte / 30)
                    .alveolesPleines(qte / 30).resteOeufs(qte % 30)
                    .cartons((qte / 30) / 12).ageMoyenJours(ageMoyen)
                    .alerteDlc(alert).build());
            totalOeufs += qte;
        }

        return StockOeufsResponse.builder()
                .parCalibre(parCalibre)
                .totalOeufs(totalOeufs)
                .totalAlveoles(totalOeufs / 30)
                .totalCartons((totalOeufs / 30) / 12)
                .alerteDlc(alerteDlc)
                .nombreLotsExpires(lotsExpires)
                .build();
    }

    // ─── DASHBOARD PONTE ──────────────────────────────────────────────

    public PonteDashboardResponse getDashboard(UUID bandeId, UUID tenantId) {
        Bande bande = bandeRepository.findByIdAndTenantId(bandeId, tenantId)
                .orElseThrow(() -> new RuntimeException("Bande introuvable"));

        LocalDate aujourd = LocalDate.now();
        LocalDate il7j    = aujourd.minusDays(7);
        LocalDate il30j   = aujourd.minusDays(30);

        // Ponte du jour
        CollecteResponse aujourd_hui = collecteRepository
                .findByBandeIdAndDate(bandeId, aujourd)
                .map(this::toCollecteResponse).orElse(null);

        // Taux de ponte moyen 7 jours
        List<CollectePonte> derniers7j = collecteRepository
                .findByBandeIdAndDateBetweenOrderByDateDesc(bandeId, il7j, aujourd);
        double tauxMoyen7j = derniers7j.stream()
                .mapToDouble(CollectePonte::getTauxPonte).average().orElse(0);
        double tauxCasseMoyen = derniers7j.stream()
                .mapToDouble(CollectePonte::getTauxCasse).average().orElse(0);

        // Courbe 30 jours
        List<CollectePonte> derniers30j = collecteRepository
                .findByBandeIdAndDateBetweenOrderByDateDesc(bandeId, il30j, aujourd);
        Map<LocalDate, CollectePonte> collecteParDate = derniers30j.stream()
                .collect(Collectors.toMap(CollectePonte::getDate, c -> c));
        List<PonteDashboardResponse.PointCourbe> courbe = new ArrayList<>();
        for (int i = 29; i >= 0; i--) {
            LocalDate d = aujourd.minusDays(i);
            CollectePonte c = collecteParDate.get(d);
            courbe.add(PonteDashboardResponse.PointCourbe.builder()
                    .date(d.format(DateTimeFormatter.ISO_LOCAL_DATE))
                    .oeufsTotal(c != null ? c.getTotalCommercialisables() : null)
                    .tauxPonte(c != null ? c.getTauxPonte() : null)
                    .build());
        }

        // Répartition calibres sur les 30 derniers jours
        long totalP = 0, totalM = 0, totalG = 0, totalS = 0;
        for (CollectePonte c : derniers30j) {
            totalP += c.getOeufsPetit(); totalM += c.getOeufsMovyen();
            totalG += c.getOeufsGros();  totalS += c.getOeufsSupGros();
        }
        long totalTous = totalP + totalM + totalG + totalS;
        Map<String, Double> repartition = new LinkedHashMap<>();
        if (totalTous > 0) {
            repartition.put("PETIT",     totalP * 100.0 / totalTous);
            repartition.put("MOYEN",     totalM * 100.0 / totalTous);
            repartition.put("GROS",      totalG * 100.0 / totalTous);
            repartition.put("SUPER_GROS",totalS * 100.0 / totalTous);
        }

        // Recettes totales
        double recettes = Optional.ofNullable(
                venteRepository.totalVentesParBande(bandeId)).orElse(0.0);

        return PonteDashboardResponse.builder()
                .totalOeufsProduitsTotal(
                        Optional.ofNullable(collecteRepository
                                .totalCommercialisablesParBande(bandeId)).orElse(0L))
                .tauxPonteMoyen(tauxMoyen7j)
                .tauxCasseMoyen(tauxCasseMoyen)
                .recettesTotales(recettes)
                .coutProductionParOeuf(0) // intégration avec Finances à faire
                .margeNette(0)
                .stockActuel(getStock(bandeId, tenantId))
                .ponteAujourdhui(aujourd_hui)
                .courbe30j(courbe)
                .repartitionCalibresPct(repartition)
                .dernieresVentes(venteRepository.findByBandeIdOrderByDateDesc(bandeId)
                        .stream().limit(5).map(this::toVenteResponse).toList())
                .build();
    }

    // ─── PRIX DE RÉFÉRENCE ────────────────────────────────────────────

    @Transactional
    public void updatePrixReference(UUID tenantId, UpdatePrixReferenceRequest req) {
        Tenant tenant = tenantRepository.findById(tenantId).orElseThrow();
        for (Map.Entry<String, Double> entry : req.getPrix().entrySet()) {
            StockOeufs.Calibre calibre = StockOeufs.Calibre.valueOf(entry.getKey());
            PrixReferenceOeufs prix = prixRepository
                    .findByTenantIdAndCalibre(tenantId, calibre)
                    .orElse(PrixReferenceOeufs.builder()
                            .tenant(tenant).calibre(calibre).prixAlveole(0).build());
            prix.setPrixAlveole(entry.getValue());
            prixRepository.save(prix);
        }
    }

    public Map<String, Double> getPrixReference(UUID tenantId) {
        return prixRepository.findByTenantId(tenantId).stream()
                .collect(Collectors.toMap(
                        p -> p.getCalibre().name(),
                        PrixReferenceOeufs::getPrixAlveole));
    }

    // ─── MAPPERS ─────────────────────────────────────────────────────

    private CollecteResponse toCollecteResponse(CollectePonte c) {
        int p = c.getOeufsPetit(), m = c.getOeufsMovyen(),
                g = c.getOeufsGros(), s = c.getOeufsSupGros();

        ConditionnementResponse cond = ConditionnementResponse.builder()
                .alveolesPetit(p/30)    .restesPetit(p%30)
                .alveolesMovyen(m/30)   .restesMovyen(m%30)
                .alveolesGros(g/30)     .restesGros(g%30)
                .alveolesSupGros(s/30)  .restesSupGros(s%30)
                .totalAlveoles((p+m+g+s)/30)
                .cartonsPossibles(((p+m+g+s)/30)/12)
                .alveolesRestantes(((p+m+g+s)/30)%12)
                .build();

        return CollecteResponse.builder()
                .id(c.getId()).date(c.getDate())
                .ageEnJours(c.getAgeEnJours())
                .oeufsPetit(p).oeufsMovyen(m).oeufsGros(g).oeufsSupGros(s)
                .oeufsCasses(c.getOeufsCasses()).oeufsDeclasses(c.getOeufsDeclasses())
                .totalCommercialisables(c.getTotalCommercialisables())
                .totalCollectes(c.getTotalCollectes())
                .tauxPonte(c.getTauxPonte())
                .tauxCasse(c.getTauxCasse())
                .conditionnement(cond)
                .observations(c.getObservations())
                .build();
    }

    private VenteResponse toVenteResponse(VenteOeufs v) {
        List<VenteResponse.LigneResponse> lignes = v.getLignes().stream()
                .map(l -> VenteResponse.LigneResponse.builder()
                        .calibre(l.getCalibre().name())
                        .nombreOeufs(l.getNombreOeufs())
                        .nombreAlveoles(l.getNombreOeufs() / 30)
                        .prixAlveole(l.getPrixAlveole())
                        .montantLigne(l.getMontantLigne())
                        .build()).toList();

        return VenteResponse.builder()
                .id(v.getId()).date(v.getDate())
                .client(v.getClient()).telephone(v.getTelephone())
                .montantTotal(v.getMontantTotal())
                .lignes(lignes).observations(v.getObservations())
                .build();
    }
}