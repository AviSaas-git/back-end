package AviSaaS.API.entity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ingredients")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(nullable = false)
    private String nom;              // "Maïs jaune"

    private String unite;            // "kg", "sac 50kg"...

    // Prix par kg — mis à jour à chaque achat
    @Column(nullable = false)
    private double prixUnitaireKg;   // en FCFA

    private String fournisseur;      // optionnel

    private String description;

    @Column(nullable = false)
    @Builder.Default
    private boolean actif = true;

    @CreationTimestamp
    private LocalDateTime createdAt;
}