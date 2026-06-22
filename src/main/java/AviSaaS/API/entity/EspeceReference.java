package AviSaaS.API.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "especes_reference")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EspeceReference {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String nom;              // "Poulet Cobb 500"

    @Column(nullable = false)
    private String icon;             // "🐔"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ModeGestion modeGestion; // LOT ou INDIVIDUEL

    private int cycleMoyenJours;            // 63 pour poulet
    private int dureeGestationJours;        // 114 pour porc
    private boolean gererReproduction;

    public enum ModeGestion { LOT, INDIVIDUEL }
}