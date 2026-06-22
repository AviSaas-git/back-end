package AviSaaS.API.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "portees")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Portee {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reproduction_id", nullable = false, unique = true)
    private Reproduction reproduction;

    @Column(nullable = false)
    private LocalDate dateMiseBas;

    @Column(nullable = false)
    private int nombreNesTotal;

    @Column(nullable = false)
    private int nombreNesVivants;

    @Column(nullable = false)
    private int nombreNesMorts;

    private Double poidsMoyenNaissanceGrammes;

    private String observations;

    @CreationTimestamp
    private LocalDateTime createdAt;
}