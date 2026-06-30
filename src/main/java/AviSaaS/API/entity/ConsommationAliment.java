package AviSaaS.API.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "consommations_aliment",
        indexes = @Index(name = "idx_conso_bande", columnList = "bande_id"))
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ConsommationAliment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bande_id", nullable = false)
    private Bande bande;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "formule_id", nullable = false)
    private FormulaAliment formule;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private double quantiteKg;

    @Column(nullable = false)
    private double prixRevientKgMoment;

    @Column(nullable = false)
    private double coutTotal;

    private String observations;

    @CreationTimestamp
    private LocalDateTime createdAt;
}