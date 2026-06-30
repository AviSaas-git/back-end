package AviSaaS.API.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "evenements_animal",
        indexes = @Index(name = "idx_evenement_animal", columnList = "animal_id"))
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class EvenementAnimal {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "animal_id", nullable = false)
    private Animal animal;

    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeEvenement type;

    // Pesée
    private Double poidsKg;

    // Prophylaxie
    private String traitement;
    private String laboratoire;
    private String dosage;
    private String voieAdministration;

    // Mortalité / cause de sortie
    private String cause;

    private String observations;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public enum TypeEvenement {
        PESEE, PROPHYLAXIE, MALADIE, MORT, VENTE, AUTRE
    }
}