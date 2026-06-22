package AviSaaS.API.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Entity
@Table(
        name = "animaux",
        indexes = @Index(name = "idx_animal_tenant", columnList = "tenant_id")
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Animal {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "espece_id", nullable = false)
    private EspeceReference espece;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batiment_id", nullable = false)
    private Batiment batiment;

    @Column(nullable = false)
    private String numero;          // "TRUIE-2025-047"

    private String nom;             // optionnel "Rosalie"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SexeEnum sexe;

    @Column(nullable = false)
    private LocalDate dateNaissance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrigineEnum origine;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StatutAnimal statut = StatutAnimal.ACTIF;

    private Double poidsActuelKg;

    // ── GÉNÉALOGIE — auto-référence ──────────────────────────────────
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pere_id")
    private Animal pere;            // null si achat

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mere_id")
    private Animal mere;            // null si achat


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portee_id")
    private Portee portee; // renseigné si né en ferme via une portée enregistrée


    @CreationTimestamp
    private LocalDateTime createdAt;

    // ── Méthodes métier ───────────────────────────────────────────────
    public int getAgeEnMois() {
        return (int) ChronoUnit.MONTHS.between(dateNaissance, LocalDate.now());
    }

    public boolean estConsanguin() {
        if (pere == null || mere == null) return false;
        UUID gpP1 = pere.getPere()  != null ? pere.getPere().getId()  : null;
        UUID gpP2 = pere.getMere()  != null ? pere.getMere().getId()  : null;
        UUID gpM1 = mere.getPere()  != null ? mere.getPere().getId()  : null;
        UUID gpM2 = mere.getMere()  != null ? mere.getMere().getId()  : null;
        return (gpP1 != null && (gpP1.equals(gpM1) || gpP1.equals(gpM2)))
                || (gpP2 != null && (gpP2.equals(gpM1) || gpP2.equals(gpM2)));
    }

    public enum SexeEnum    { MALE, FEMELLE , INDETERMINE}
    public enum OrigineEnum { ACHAT, NAISSANCE_FERME }
    public enum StatutAnimal{ ACTIF, VENDU, MORT, REPRODUCTEUR }
}