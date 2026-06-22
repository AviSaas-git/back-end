package AviSaaS.API.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Entity
@Table(name = "suivi_prophylaxie")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SuiviProphylaxie {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bande_id", nullable = false)
    private Bande bande;

    @Column(nullable = false)
    private LocalDate dateApplication;

    @Column(nullable = false)
    private String traitement;      // "Gumboro", "Newcastle"...

    private String laboratoire;     // "Ceva", "MSD"...

    @Column(nullable = false)
    private String dosage;          // "0.5 ml / L eau de boisson"

    @Enumerated(EnumType.STRING)
    private VoieAdministration voieAdministration;

    private String observations;

    @CreationTimestamp
    private LocalDateTime createdAt;

    // Âge de la bande calculé automatiquement — jamais saisi manuellement
    public int getAgeEnJoursAuTraitement() {
        return (int) ChronoUnit.DAYS.between(
                bande.getDateArrivee(), dateApplication);
    }

    public enum VoieAdministration {
        EAU_DE_BOISSON, INJECTION, SPRAY, ALIMENT, OCULAIRE_NASALE
    }
}