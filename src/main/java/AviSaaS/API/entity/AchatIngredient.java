package AviSaaS.API.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "achats_ingredient")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AchatIngredient {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private double quantiteKg;

    @Column(nullable = false)
    private double prixUnitaireKg;      // prix payé ce jour

    @Column(nullable = false)
    private double montantTotal;        // quantiteKg * prixUnitaireKg

    private String fournisseur;

    private String observations;

    @CreationTimestamp
    private LocalDateTime createdAt;
}