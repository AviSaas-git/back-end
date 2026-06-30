package AviSaaS.API.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "formulas_aliment")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class FormulaAliment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(nullable = false)
    private String nom;              // "Démarrage 0-21j"

    private String description;      // "Phase démarrage poulets de chair"

    // Pour quelle espèce cette formule est prévue (optionnel)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "espece_id")
    private EspeceReference espece;

    // Phase d'élevage : DEMARRAGE, CROISSANCE, FINITION, UNIQUE
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private PhaseElevage phase = PhaseElevage.UNIQUE;

    // Composition : liste des ingrédients avec leurs proportions
    @OneToMany(mappedBy = "formule", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<FormulaIngredient> composition = new ArrayList<>();

    @Column(nullable = false)
    @Builder.Default
    private boolean actif = true;

    @CreationTimestamp
    private LocalDateTime createdAt;

    // Calcule le prix de revient par kg de cette formule
    // basé sur les prix actuels des ingrédients
    public double getPrixRevientKg() {
        double total = 0;
        for (FormulaIngredient fi : composition) {
            // proportionPour100kg / 100 = proportion dans 1 kg de mélange
            total += (fi.getProportionPour100kg() / 100.0)
                    * fi.getIngredient().getPrixUnitaireKg();
        }
        return total;
    }

    public enum PhaseElevage {
        DEMARRAGE, CROISSANCE, FINITION, UNIQUE, GESTATION, LACTATION
    }
}