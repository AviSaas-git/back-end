package AviSaaS.API.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Entity
@Table(name = "collectes_ponte",
        indexes = @Index(name = "idx_collecte_bande", columnList = "bande_id"))
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CollectePonte {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bande_id", nullable = false)
    private Bande bande;

    @Column(nullable = false)
    private LocalDate date;

    // ── 4 calibres ───────────────────────────────────────────────────
    @Column(nullable = false) private int oeufsPetit;     // < 53g
    @Column(nullable = false) private int oeufsMovyen;    // 53-63g
    @Column(nullable = false) private int oeufsGros;      // 63-73g
    @Column(nullable = false) private int oeufsSupGros;   // > 73g

    // ── Casses et déclassés ──────────────────────────────────────────
    @Column(nullable = false) private int oeufsCasses;    // non commercialisables
    @Column(nullable = false) private int oeufsDeclasses; // fêlés, tachés → usage interne

    private String observations;

    @CreationTimestamp
    private LocalDateTime createdAt;

    // ── Méthodes calculées ───────────────────────────────────────────
    public int getTotalCollectes() {
        return oeufsPetit + oeufsMovyen + oeufsGros + oeufsSupGros
                + oeufsCasses + oeufsDeclasses;
    }

    public int getTotalCommercialisables() {
        return oeufsPetit + oeufsMovyen + oeufsGros + oeufsSupGros;
    }

    public double getTauxPonte() {
        int effectif = bande.getEffectifActuel();
        if (effectif <= 0) return 0;
        return (getTotalCommercialisables() / (double) effectif) * 100;
    }

    public double getTauxCasse() {
        int total = getTotalCollectes();
        if (total <= 0) return 0;
        return (oeufsCasses / (double) total) * 100;
    }

    public int getAgeEnJours() {
        return (int) ChronoUnit.DAYS.between(bande.getDateArrivee(), date);
    }
}