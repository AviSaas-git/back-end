package AviSaaS.API.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "suivi_mortalite")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SuiviMortalite {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bande_id", nullable = false)
    private Bande bande;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private int nombreMorts;

    private String cause; // chaleur, maladie, écrasement...

    @CreationTimestamp
    private LocalDateTime createdAt;
}