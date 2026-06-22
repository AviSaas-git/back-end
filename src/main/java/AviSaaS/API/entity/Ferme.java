package AviSaaS.API.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "fermes",
        indexes = @Index(name = "idx_ferme_tenant", columnList = "tenant_id")
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ferme {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(nullable = false)
    private String nom;

    private String localisation;

    private Double surfaceM2;

    @Column(nullable = false)
    private int capaciteMax;

    @CreationTimestamp
    private LocalDateTime createdAt;
}