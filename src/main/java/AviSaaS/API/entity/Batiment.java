package AviSaaS.API.entity;

import AviSaaS.API.entity.Ferme;
import jakarta.persistence.*;
import lombok.*;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.util.UUID;

@Entity
@Table(
        name = "batiments",
        indexes = @Index(name = "idx_batiment_ferme", columnList = "ferme_id")
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Batiment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ferme_id", nullable = false)
    private Ferme ferme;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private int capacite;

    private Double surfaceM2;

    private String typeChauffage;

    @Column(nullable = false)
    @Builder.Default
    private String type = "poulailler";

    @Enumerated(EnumType.STRING)
    private ModeOccupation modeOccupation;
}