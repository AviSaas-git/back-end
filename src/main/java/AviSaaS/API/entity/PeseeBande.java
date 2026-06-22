package AviSaaS.API.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "pesees_bande")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PeseeBande {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bande_id", nullable = false)
    private Bande bande;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private int ageEnJours;          // figé au moment de la pesée

    @Column(nullable = false)
    private int nombreSujetsPeses;   // échantillon pesé (ex: 20 oiseaux)

    @Column(nullable = false)
    private double poidsMoyenGrammes;

    @CreationTimestamp
    private LocalDateTime createdAt;
}