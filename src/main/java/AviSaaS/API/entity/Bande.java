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
        name = "bandes",
        indexes = @Index(name = "idx_bande_tenant", columnList = "tenant_id")
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Bande {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "UUID") // Optionnel, recommandé pour PostgreSQL
    private UUID id;

    private LocalDate dateSortie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "espece_id", nullable = false)
    private EspeceReference espece;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batiment_id", nullable = false)
    private Batiment batiment;

    @Column(nullable = false, unique = true)
    private String reference;        // Ex: "BAND-2025-001"

    private String race;             // Ex: "Cobb 500"

    @Column(nullable = false)
    private int effectifInitial;

    @Column(nullable = false)
    private int effectifActuel;

    @Column(nullable = false)
    private LocalDate dateArrivee;

    private LocalDate dateCloture;   // null si active

    private String fournisseur;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StatutBande statut = StatutBande.ACTIVE;

    @CreationTimestamp
    private LocalDateTime createdAt;


    // ── Méthodes métier ───────────────────────────────────────────────
    public double getTauxMortalite() {
        if (effectifInitial == 0) return 0;
        return ((double)(effectifInitial - effectifActuel)
                / effectifInitial) * 100;
    }

    public int getAgeEnJours() {
        if (dateArrivee == null) return 0; // Sécurité anti-NullPointerException
        return (int) ChronoUnit.DAYS.between(dateArrivee, LocalDate.now());
    }

    public int getProgressionPct() {
        if (espece == null || espece.getCycleMoyenJours() <= 0) return 0;
        return (int) Math.min(100,
                (getAgeEnJours() * 100.0) / espece.getCycleMoyenJours());
    }

    public enum StatutBande { ACTIVE, CLOTUREE }
}