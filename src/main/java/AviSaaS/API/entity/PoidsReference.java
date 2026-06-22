package AviSaaS.API.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
        name = "poids_reference",
        uniqueConstraints = @UniqueConstraint(columnNames = {"espece_id", "jour"})
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PoidsReference {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "espece_id", nullable = false)
    private EspeceReference espece;

    @Column(nullable = false)
    private int jour;                  // J0, J1, J2... selon la fiche

    @Column(nullable = false)
    private double poidsGrammesCible;  // poids théorique ce jour-là
}