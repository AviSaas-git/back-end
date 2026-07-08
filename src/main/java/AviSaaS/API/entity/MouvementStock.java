package AviSaaS.API.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "mouvements_stock")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class MouvementStock {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_item_id", nullable = false)
    private StockItem item;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeMouvement type; // ENTREE ou SORTIE

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private double quantite;

    private String motif; // "Achat", "Utilisation bâtiment A"...

    @CreationTimestamp
    private LocalDateTime createdAt;

    public enum TypeMouvement { ENTREE, SORTIE }
}