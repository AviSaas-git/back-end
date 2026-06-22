package AviSaaS.API.service;


import AviSaaS.API.dto.request.CreateReproductionRequest;
import AviSaaS.API.dto.request.EnregistrerPorteeRequest;
import AviSaaS.API.dto.response.PorteeResponse;
import AviSaaS.API.dto.response.ReproductionResponse;
import AviSaaS.API.entity.*;
import AviSaaS.API.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReproductionService {

    private final ReproductionRepository reproductionRepository;
    private final PorteeRepository       porteeRepository;
    private final AnimalRepository       animalRepository;
    private final TenantRepository       tenantRepository;

    private static final List<Reproduction.StatutRepro> STATUTS_EN_COURS =
            List.of(Reproduction.StatutRepro.SAILLIE, Reproduction.StatutRepro.GESTANTE);

    @Transactional
    public ReproductionResponse creer(CreateReproductionRequest req, UUID tenantId) {

        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant introuvable"));

        Animal femelle = animalRepository.findByIdAndTenantId(req.getFemelleId(), tenantId)
                .orElseThrow(() -> new RuntimeException("Femelle introuvable"));

        if (femelle.getSexe() != Animal.SexeEnum.FEMELLE) {
            throw new RuntimeException("L'animal sélectionné n'est pas une femelle");
        }

        // Empêche une double gestation sur la même femelle
        if (reproductionRepository.existsByFemelleIdAndStatutIn(femelle.getId(), STATUTS_EN_COURS)) {
            throw new RuntimeException(femelle.getNumero() + " a déjà une gestation en cours");
        }

        Animal male = null;
        if (req.getMaleId() != null) {
            male = animalRepository.findByIdAndTenantId(req.getMaleId(), tenantId)
                    .orElseThrow(() -> new RuntimeException("Mâle introuvable"));
            if (male.getSexe() != Animal.SexeEnum.MALE) {
                throw new RuntimeException("L'animal sélectionné comme père n'est pas un mâle");
            }
        }

        // ── Calcul automatique de la date de mise bas ──
        int dureeGestation = femelle.getEspece().getDureeGestationJours();
        LocalDate dateMiseBasPrevue = req.getDateSaillie().plusDays(dureeGestation);

        Reproduction repro = Reproduction.builder()
                .tenant(tenant)
                .femelle(femelle)
                .male(male)
                .dateSaillie(req.getDateSaillie())
                .dateMiseBasPrevue(dateMiseBasPrevue)
                .statut(Reproduction.StatutRepro.SAILLIE)
                .observations(req.getObservations())
                .build();

        repro = reproductionRepository.save(repro);
        return toResponse(repro);
    }

    @Transactional
    public ReproductionResponse confirmerGestation(UUID id, UUID tenantId) {
        Reproduction repro = getOwned(id, tenantId);
        if (repro.getStatut() != Reproduction.StatutRepro.SAILLIE) {
            throw new RuntimeException("Seule une saillie en attente peut être confirmée");
        }
        repro.setStatut(Reproduction.StatutRepro.GESTANTE);
        reproductionRepository.save(repro);
        return toResponse(repro);
    }

    @Transactional
    public ReproductionResponse declarerEchec(UUID id, UUID tenantId) {
        Reproduction repro = getOwned(id, tenantId);
        if (repro.getStatut() == Reproduction.StatutRepro.MISE_BAS) {
            throw new RuntimeException("Une mise bas a déjà été enregistrée");
        }
        repro.setStatut(Reproduction.StatutRepro.ECHEC);
        reproductionRepository.save(repro);
        return toResponse(repro);
    }

    public List<ReproductionResponse> lister(UUID tenantId) {
        return reproductionRepository.findByTenantIdOrderByDateSaillieDesc(tenantId)
                .stream().map(this::toResponse).toList();
    }

    public List<ReproductionResponse> getCalendrier(UUID tenantId, int joursAVenir) {
        LocalDate aujourdhui = LocalDate.now();
        return reproductionRepository.findByTenantIdAndStatutAndDateMiseBasPrevueBetween(
                tenantId, Reproduction.StatutRepro.GESTANTE,
                aujourdhui.minusDays(7), aujourdhui.plusDays(joursAVenir)
        ).stream().map(this::toResponse).toList();
    }

    // ── Enregistrement de la portée + génération auto des fiches animaux ──
    @Transactional
    public PorteeResponse enregistrerPortee(UUID reproductionId, UUID tenantId,
                                            EnregistrerPorteeRequest req) {

        Reproduction repro = getOwned(reproductionId, tenantId);

        if (repro.getPortee() != null) {
            throw new RuntimeException("Une portée a déjà été enregistrée");
        }
        if (repro.getStatut() == Reproduction.StatutRepro.ECHEC) {
            throw new RuntimeException("Cette gestation a été déclarée en échec");
        }

        int total = req.getNombreNesVivants() + req.getNombreNesMorts();

        Portee portee = Portee.builder()
                .reproduction(repro)
                .dateMiseBas(req.getDateMiseBas())
                .nombreNesTotal(total)
                .nombreNesVivants(req.getNombreNesVivants())
                .nombreNesMorts(req.getNombreNesMorts())
                .poidsMoyenNaissanceGrammes(req.getPoidsMoyenNaissanceGrammes())
                .observations(req.getObservations())
                .build();
        portee = porteeRepository.save(portee);

        repro.setStatut(Reproduction.StatutRepro.MISE_BAS);
        repro.setPortee(portee);
        reproductionRepository.save(repro);

        List<String> numerosGeneres = genererFichesAnimaux(
                repro, portee, req.getNombreNesVivants(), req.getPrefixeNumero(), tenantId);

        return PorteeResponse.builder()
                .id(portee.getId())
                .dateMiseBas(portee.getDateMiseBas())
                .nombreNesTotal(portee.getNombreNesTotal())
                .nombreNesVivants(portee.getNombreNesVivants())
                .nombreNesMorts(portee.getNombreNesMorts())
                .poidsMoyenNaissanceGrammes(portee.getPoidsMoyenNaissanceGrammes())
                .numerosAnimauxGeneres(numerosGeneres)
                .build();
    }

    // ── Génère 1 fiche Animal par né vivant, sexe à déterminer plus tard ──
    private List<String> genererFichesAnimaux(Reproduction repro, Portee portee,
                                              int nombreNesVivants, String prefixeFourni,
                                              UUID tenantId) {
        if (nombreNesVivants <= 0) return List.of();

        Animal femelle = repro.getFemelle();
        EspeceReference espece = femelle.getEspece();
        Tenant tenant = femelle.getTenant();
        int annee = portee.getDateMiseBas().getYear();

        String prefixe = (prefixeFourni != null && !prefixeFourni.isBlank())
                ? prefixeFourni.toUpperCase()
                : prefixeDepuisEspece(espece.getNom());

        long dejaExistants = animalRepository.countByTenantIdAndNumeroStartingWith(
                tenantId, prefixe + "-" + annee);

        List<String> numerosGeneres = new ArrayList<>();

        for (int i = 1; i <= nombreNesVivants; i++) {
            String numero = String.format("%s-%d-%03d", prefixe, annee, dejaExistants + i);

            Animal nouveau = Animal.builder()
                    .tenant(tenant)
                    .espece(espece)
                    .batiment(femelle.getBatiment())
                    .numero(numero)
                    .sexe(Animal.SexeEnum.INDETERMINE) // sexage à faire après naissance
                    .dateNaissance(portee.getDateMiseBas())
                    .origine(Animal.OrigineEnum.NAISSANCE_FERME)
                    .pere(repro.getMale())
                    .mere(femelle)
                    .portee(portee)
                    .statut(Animal.StatutAnimal.ACTIF)
                    .build();

            animalRepository.save(nouveau);
            numerosGeneres.add(numero);
        }

        return numerosGeneres;
    }

    private String prefixeDepuisEspece(String nomEspece) {
        String lettres = nomEspece.replaceAll("[^A-Za-zÀ-ÿ]", "");
        return lettres.length() >= 3
                ? lettres.substring(0, 3).toUpperCase()
                : lettres.toUpperCase();
    }

    private Reproduction getOwned(UUID id, UUID tenantId) {
        return reproductionRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new RuntimeException("Reproduction introuvable"));
    }

    private ReproductionResponse toResponse(Reproduction r) {
        return ReproductionResponse.builder()
                .id(r.getId())
                .femelleNumero(r.getFemelle().getNumero())
                .femelleNom(r.getFemelle().getNom())
                .maleNumero(r.getMale() != null ? r.getMale().getNumero() : null)
                .especeNom(r.getFemelle().getEspece().getNom())
                .especeIcon(r.getFemelle().getEspece().getIcon())
                .dateSaillie(r.getDateSaillie())
                .dateMiseBasPrevue(r.getDateMiseBasPrevue())
                .joursRestants((int) ChronoUnit.DAYS.between(LocalDate.now(), r.getDateMiseBasPrevue()))
                .statut(r.getStatut().name())
                .observations(r.getObservations())
                .porteeEnregistree(r.getPortee() != null)
                .build();
    }
}