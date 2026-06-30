package AviSaaS.API.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "formula_ingredients")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class FormulaIngredient {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "formule_id", nullable = false)
    private FormulaAliment formule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;

    // Quantité en kg pour 100 kg de mélange total
    // La somme de toutes les proportions devrait = 100
    @Column(nullable = false)
    private double proportionPour100kg;  // ex: 55.0 pour le maïs

    private String observations;         // ex: "Broyage fin"
}