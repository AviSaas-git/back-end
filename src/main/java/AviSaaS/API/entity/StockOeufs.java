package AviSaaS.API.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "stock_oeufs",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"bande_id", "calibre", "date_collecte"}))
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class StockOeufs {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bande_id", nullable = false)
    private Bande bande;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Calibre calibre;

    // Date à laquelle ces œufs ont été collectés — pour la DLC
    @Column(nullable = false)
    private LocalDate dateCollecte;

    // Quantité restante de ce lot (decrementée à chaque vente)
    @Column(nullable = false)
    private int quantiteDisponible;

    public enum Calibre {
        PETIT, MOYEN, GROS, SUPER_GROS
    }

    // ── Conditionnement calculé ──────────────────────────────────────
    public int getAlveoles()           { return quantiteDisponible / 30; }
    public int getOeufsRestants()      { return quantiteDisponible % 30; }
    public int getCartons()            { return getAlveoles() / 12; }
    public int getAlveolesRestantes()  { return getAlveoles() % 12; }

    // DLC implicite à 21 jours
    public boolean estEnAlerte() {
        return LocalDate.now().isAfter(dateCollecte.plusDays(15));
    }
    public boolean estExpire() {
        return LocalDate.now().isAfter(dateCollecte.plusDays(21));
    }
    public int getAgeLot() {
        return (int) java.time.temporal.ChronoUnit.DAYS
                .between(dateCollecte, LocalDate.now());
    }
}