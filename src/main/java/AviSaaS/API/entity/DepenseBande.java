package AviSaaS.API.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "depenses_bande",
        indexes = @Index(name = "idx_depense_bande", columnList = "bande_id"))
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class DepenseBande {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bande_id", nullable = false)
    private Bande bande;

    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategorieDepense categorie;

    @Column(nullable = false)
    private String sousCategorie;   // libellé libre

    @Column(nullable = false)
    private double montant;         // en FCFA

    private String fournisseur;     // optionnel
    private String description;     // optionnel

    @CreationTimestamp
    private LocalDateTime createdAt;

    public enum CategorieDepense {
        PRODUCTION,    // aliment, prophylaxie, chauffage, transport...
        CHARGE_FIXE    // bâtiment, salaire, électricité, eau...
    }
}