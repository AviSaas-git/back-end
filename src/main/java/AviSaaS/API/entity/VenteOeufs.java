package AviSaaS.API.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "ventes_oeufs")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class VenteOeufs {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bande_id", nullable = false)
    private Bande bande;

    @Column(nullable = false)
    private LocalDate date;

    private String client;       // nom du client (optionnel)
    private String telephone;    // téléphone client (optionnel)

    @Column(nullable = false)
    private double montantTotal; // calculé depuis les lignes

    private String observations;

    @OneToMany(mappedBy = "vente", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<VenteLigneOeufs> lignes = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;
}