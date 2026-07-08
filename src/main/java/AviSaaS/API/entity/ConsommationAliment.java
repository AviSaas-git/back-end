package AviSaaS.API.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "consommations_aliment")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ConsommationAliment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // L'un OU l'autre est rempli, jamais les deux
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bande_id")
    private Bande bande;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "animal_id")
    private Animal animal;

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