package AviSaaS.API.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "stock_items")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class StockItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(nullable = false)
    private String nom;             // "Seringues 5ml"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategorieStock categorie;

    private String unite;           // "unité", "litre", "boîte"...

    @Column(nullable = false)
    private double quantiteActuelle;

    @Column(nullable = false)
    private double seuilAlerte;     // déclenche une alerte stock bas

    private double prixUnitaire;    // optionnel — pour valoriser le stock

    private String fournisseur;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public boolean estEnAlerte() {
        return quantiteActuelle <= seuilAlerte;
    }

    public enum CategorieStock {
        MEDICAMENT, MATERIEL, EQUIPEMENT, CONSOMMABLE, EMBALLAGE
    }
}