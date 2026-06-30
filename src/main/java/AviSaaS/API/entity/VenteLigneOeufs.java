package AviSaaS.API.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "vente_lignes_oeufs")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class VenteLigneOeufs {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vente_id", nullable = false)
    private VenteOeufs vente;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StockOeufs.Calibre calibre;

    @Column(nullable = false)
    private int nombreOeufs;     // en unités

    // Prix par alvéole (30 œufs) au moment de la vente — figé
    @Column(nullable = false)
    private double prixAlveole;

    @Column(nullable = false)
    private double montantLigne; // calculé = (nombreOeufs/30) * prixAlveole
}