package AviSaaS.API.service;

import AviSaaS.API.dto.request.*;
import AviSaaS.API.dto.response.*;
import AviSaaS.API.entity.*;
import AviSaaS.API.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AlimentationService {

    private final IngredientRepository      ingredientRepository;
    private final FormulaAlimentRepository  formulaRepository;
    private final ConsommationAlimentRepository consommationRepository;
    private final AchatIngredientRepository achatRepository;
    private final BandeRepository           bandeRepository;
    private final TenantRepository          tenantRepository;
    private final EspeceReferenceRepository especeRepository;
    private final DepenseBandeRepository depenseRepository;
    // ─── INGRÉDIENTS ─────────────────────────────────────────────────
// Ajoute AnimalRepository dans le constructeur (@RequiredArgsConstructor le fait automatiquement)
    private final AnimalRepository animalRepository;
    @Transactional
    public IngredientResponse creerIngredient(CreateIngredientRequest req, UUID tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant introuvable"));

        Ingredient ing = Ingredient.builder()
                .tenant(tenant)
                .nom(req.getNom())
                .unite(req.getUnite())
                .prixUnitaireKg(req.getPrixUnitaireKg())
                .fournisseur(req.getFournisseur())
                .description(req.getDescription())
                .build();

        return toIngredientResponse(ingredientRepository.save(ing));
    }

    public List<IngredientResponse> listerIngredients(UUID tenantId) {
        return ingredientRepository.findByTenantIdAndActifTrueOrderByNomAsc(tenantId)
                .stream().map(this::toIngredientResponse).toList();
    }

    @Transactional
    public IngredientResponse enregistrerAchat(UUID ingredientId, UUID tenantId,
                                               AchatIngredientRequest req) {
        Ingredient ing = ingredientRepository.findByIdAndTenantId(ingredientId, tenantId)
                .orElseThrow(() -> new RuntimeException("Ingrédient introuvable"));

        // Enregistre l'historique d'achat
        AchatIngredient achat = AchatIngredient.builder()
                .ingredient(ing)
                .date(req.getDate())
                .quantiteKg(req.getQuantiteKg())
                .prixUnitaireKg(req.getPrixUnitaireKg())
                .montantTotal(req.getQuantiteKg() * req.getPrixUnitaireKg())
                .fournisseur(req.getFournisseur())
                .observations(req.getObservations())
                .build();
        achatRepository.save(achat);

        // Met à jour le prix courant de l'ingrédient
        ing.setPrixUnitaireKg(req.getPrixUnitaireKg());
        if (req.getFournisseur() != null) ing.setFournisseur(req.getFournisseur());
        ingredientRepository.save(ing);

        return toIngredientResponse(ing);
    }

    // ─── FORMULES ────────────────────────────────────────────────────

    @Transactional
    public FormulaResponse creerFormule(CreateFormulaRequest req, UUID tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant introuvable"));

        EspeceReference espece = null;
        if (req.getEspeceId() != null) {
            espece = especeRepository.findById(req.getEspeceId()).orElse(null);
        }

        FormulaAliment.PhaseElevage phase = FormulaAliment.PhaseElevage.UNIQUE;
        if (req.getPhase() != null) {
            try { phase = FormulaAliment.PhaseElevage.valueOf(req.getPhase()); }
            catch (IllegalArgumentException ignored) {}
        }

        FormulaAliment formule = FormulaAliment.builder()
                .tenant(tenant)
                .nom(req.getNom())
                .description(req.getDescription())
                .espece(espece)
                .phase(phase)
                .build();

        // Composition
        for (CreateFormulaRequest.LigneFormule ligne : req.getComposition()) {
            Ingredient ing = ingredientRepository
                    .findByIdAndTenantId(ligne.getIngredientId(), tenantId)
                    .orElseThrow(() -> new RuntimeException(
                            "Ingrédient introuvable : " + ligne.getIngredientId()));

            FormulaIngredient fi = FormulaIngredient.builder()
                    .formule(formule)
                    .ingredient(ing)
                    .proportionPour100kg(ligne.getProportionPour100kg())
                    .observations(ligne.getObservations())
                    .build();
            formule.getComposition().add(fi);
        }

        return toFormulaResponse(formulaRepository.save(formule));
    }

    public List<FormulaResponse> listerFormules(UUID tenantId) {
        return formulaRepository.findByTenantIdAndActifTrueOrderByNomAsc(tenantId)
                .stream().map(this::toFormulaResponse).toList();
    }

    // ─── CONSOMMATION PAR BANDE ──────────────────────────────────────

    @Transactional
    public ConsommationResponse enregistrerConsommation(UUID bandeId, UUID tenantId,
                                                        CreateConsommationRequest req) {

        Bande bande = bandeRepository.findByIdAndTenantId(bandeId, tenantId)
                .orElseThrow(() -> new RuntimeException("Bande introuvable"));

        FormulaAliment formule = formulaRepository
                .findByIdAndTenantId(req.getFormulaId(), tenantId)
                .orElseThrow(() -> new RuntimeException("Formule introuvable"));

        double prixMoment = formule.getPrixRevientKg();
        double coutTotal  = req.getQuantiteKg() * prixMoment;

        ConsommationAliment conso = ConsommationAliment.builder()
                .bande(bande)
                .formule(formule)
                .date(req.getDate())
                .quantiteKg(req.getQuantiteKg())
                .prixRevientKgMoment(prixMoment)
                .coutTotal(coutTotal)
                .observations(req.getObservations())
                .build();

        conso = consommationRepository.save(conso);

        // ── Création automatique dans les finances ───────────────────────
        // L'éleveur ne saisit pas deux fois — c'est automatique
        DepenseBande depense = DepenseBande.builder()
                .bande(bande)
                .date(req.getDate())
                .categorie(DepenseBande.CategorieDepense.PRODUCTION)
                .sousCategorie("Alimentation")
                .montant(coutTotal)
                .description("Formule : " + formule.getNom()
                        + " — " + req.getQuantiteKg() + " kg")
                .build();
        depenseRepository.save(depense);
        // ────────────────────────────────────────────────────────────────

        return toConsommationResponse(conso);
    }

    public AlimentationBandeResponse getAlimentationBande(UUID bandeId, UUID tenantId) {
        Bande bande = bandeRepository.findByIdAndTenantId(bandeId, tenantId)
                .orElseThrow(() -> new RuntimeException("Bande introuvable"));

        List<ConsommationAliment> consos =
                consommationRepository.findByBandeIdOrderByDateDesc(bandeId);

        double totalKg   = consos.stream().mapToDouble(ConsommationAliment::getQuantiteKg).sum();
        double totalCout = consos.stream().mapToDouble(ConsommationAliment::getCoutTotal).sum();

        // Coût par oiseau
        double coutParOiseau = bande.getEffectifInitial() > 0
                ? totalKg / bande.getEffectifInitial() : 0;

        // Coût par formule
        Map<String, Double> coutParFormule = consos.stream()
                .collect(Collectors.groupingBy(
                        c -> c.getFormule().getNom(),
                        Collectors.summingDouble(ConsommationAliment::getCoutTotal)));

        return AlimentationBandeResponse.builder()
                .totalKgConsommes(totalKg)
                .coutTotalAlimentation(totalCout)
                .coutParKgVifMoyen(0)   // calculé si pesée disponible
                .consommationParOiseauKg(coutParOiseau)
                .coutParFormule(coutParFormule)
                .historique(consos.stream().map(this::toConsommationResponse).toList())
                .build();
    }

    // ─── MAPPERS ─────────────────────────────────────────────────────

    private IngredientResponse toIngredientResponse(Ingredient i) {
        return IngredientResponse.builder()
                .id(i.getId()).nom(i.getNom())
                .unite(i.getUnite()).prixUnitaireKg(i.getPrixUnitaireKg())
                .fournisseur(i.getFournisseur()).description(i.getDescription())
                .build();
    }

    private FormulaResponse toFormulaResponse(FormulaAliment f) {
        double totalProportion = f.getComposition().stream()
                .mapToDouble(FormulaIngredient::getProportionPour100kg).sum();

        List<FormulaResponse.LigneFormulaResponse> lignes = f.getComposition().stream()
                .map(fi -> {
                    double contrib = (fi.getProportionPour100kg() / 100.0)
                            * fi.getIngredient().getPrixUnitaireKg() * 100;
                    return FormulaResponse.LigneFormulaResponse.builder()
                            .ingredientId(fi.getIngredient().getId())
                            .ingredientNom(fi.getIngredient().getNom())
                            .ingredientUnite(fi.getIngredient().getUnite())
                            .proportionPour100kg(fi.getProportionPour100kg())
                            .prixIngredientKg(fi.getIngredient().getPrixUnitaireKg())
                            .coutContribution(contrib)
                            .observations(fi.getObservations())
                            .build();
                }).toList();

        return FormulaResponse.builder()
                .id(f.getId()).nom(f.getNom())
                .description(f.getDescription())
                .especeNom(f.getEspece() != null ? f.getEspece().getNom() : null)
                .phase(f.getPhase().name())
                .prixRevientKg(f.getPrixRevientKg())
                .proportionTotale(totalProportion)
                .composition(lignes)
                .build();
    }

    private ConsommationResponse toConsommationResponse(ConsommationAliment c) {
        return ConsommationResponse.builder()
                .id(c.getId()).date(c.getDate())
                .nom(c.getFormule().getNom())
                .quantiteKg(c.getQuantiteKg())
                .prixRevientKgMoment(c.getPrixRevientKgMoment())
                .coutTotal(c.getCoutTotal())
                .observations(c.getObservations())
                .build();
    }



    @Transactional
    public ConsommationResponse enregistrerConsommationAnimal(UUID animalId, UUID tenantId,
                                                              CreateConsommationRequest req) {
        Animal animal = animalRepository.findByIdAndTenantId(animalId, tenantId)
                .orElseThrow(() -> new RuntimeException("Animal introuvable"));

        FormulaAliment formule = formulaRepository
                .findByIdAndTenantId(req.getFormulaId(), tenantId)
                .orElseThrow(() -> new RuntimeException("Formule introuvable"));

        double prixMoment = formule.getPrixRevientKg();
        double coutTotal  = req.getQuantiteKg() * prixMoment;

        ConsommationAliment conso = ConsommationAliment.builder()
                .animal(animal)
                .formule(formule)
                .date(req.getDate())
                .quantiteKg(req.getQuantiteKg())
                .prixRevientKgMoment(prixMoment)
                .coutTotal(coutTotal)
                .observations(req.getObservations())
                .build();

        conso = consommationRepository.save(conso);
        return toConsommationResponse(conso);
    }

    public AlimentationBandeResponse getAlimentationAnimal(UUID animalId, UUID tenantId) {
        animalRepository.findByIdAndTenantId(animalId, tenantId)
                .orElseThrow(() -> new RuntimeException("Animal introuvable"));

        List<ConsommationAliment> consos =
                consommationRepository.findByAnimalIdOrderByDateDesc(animalId);

        double totalKg   = consos.stream().mapToDouble(ConsommationAliment::getQuantiteKg).sum();
        double totalCout = consos.stream().mapToDouble(ConsommationAliment::getCoutTotal).sum();

        Map<String, Double> coutParFormule = consos.stream()
                .collect(Collectors.groupingBy(
                        c -> c.getFormule().getNom(),
                        Collectors.summingDouble(ConsommationAliment::getCoutTotal)));

        return AlimentationBandeResponse.builder()
                .totalKgConsommes(totalKg)
                .coutTotalAlimentation(totalCout)
                .coutParKgVifMoyen(0)
                .consommationParOiseauKg(totalKg) // ici = total animal (1 seul sujet)
                .coutParFormule(coutParFormule)
                .historique(consos.stream().map(this::toConsommationResponse).toList())
                .build();
    }

    //

    @Transactional
    public IngredientResponse modifierIngredient(UUID id, UUID tenantId,
                                                 UpdateIngredientRequest req) {
        Ingredient ing = ingredientRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new RuntimeException("Ingrédient introuvable"));

        if (req.getNom()            != null) ing.setNom(req.getNom());
        if (req.getUnite()          != null) ing.setUnite(req.getUnite());
        if (req.getPrixUnitaireKg() != null) ing.setPrixUnitaireKg(req.getPrixUnitaireKg());
        if (req.getFournisseur()    != null) ing.setFournisseur(req.getFournisseur());
        if (req.getDescription()    != null) ing.setDescription(req.getDescription());
        if (req.getActif()          != null) ing.setActif(req.getActif());

        return toIngredientResponse(ingredientRepository.save(ing));
    }

    @Transactional
    public void supprimerIngredient(UUID id, UUID tenantId) {
        Ingredient ing = ingredientRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new RuntimeException("Ingrédient introuvable"));
        // Soft delete — on désactive au lieu de supprimer
        // pour préserver l'historique des consommations
        ing.setActif(false);
        ingredientRepository.save(ing);
    }


    @Transactional
    public void supprimerFormule(UUID id, UUID tenantId) {
        FormulaAliment formule = formulaRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new RuntimeException("Formule introuvable"));
        // Soft delete — préserve l'historique des consommations
        formule.setActif(false);
        formulaRepository.save(formule);
    }
}