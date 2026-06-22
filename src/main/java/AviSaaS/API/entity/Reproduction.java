package AviSaaS.API.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "reproductions", indexes = @Index(name = "idx_repro_tenant", columnList = "tenant_id"))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reproduction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "femelle_id", nullable = false)
    private Animal femelle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "male_id")
    private Animal male; // null si IA ou géniteur inconnu

    @Column(nullable = false)
    private LocalDate dateSaillie;

    @Column(nullable = false)
    private LocalDate dateMiseBasPrevue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StatutRepro statut = StatutRepro.SAILLIE;

    private String observations;

    @OneToOne(mappedBy = "reproduction", cascade = CascadeType.ALL)
    private Portee portee;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public enum StatutRepro { SAILLIE, GESTANTE, MISE_BAS, ECHEC }
}