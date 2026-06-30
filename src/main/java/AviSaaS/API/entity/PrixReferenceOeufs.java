package AviSaaS.API.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "prix_reference_oeufs",
        uniqueConstraints = @UniqueConstraint(columnNames = {"tenant_id", "calibre"}))
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PrixReferenceOeufs {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StockOeufs.Calibre calibre;

    // Prix par alvéole de 30 œufs
    @Column(nullable = false)
    private double prixAlveole;

    // Prix par carton de 12 alvéoles (optionnel — peut différer de prixAlveole * 12)
    private Double prixCarton;
}