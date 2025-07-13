package itu.biblio.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "demande_prolongement")
public class DemandeProlongement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "emprunt_id")
    private Emprunt emprunt;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;

    @Column(name = "date_demande")
    private LocalDate dateDemande;

    @Column(name = "date_prolongement_demandee")
    private LocalDate dateProlongementDemandee;

    @Column(name = "statut")
    private String statut; // en_attente, acceptee, refusee

    @Column(name = "motif")
    private String motif;

    @Column(name = "date_validation")
    private LocalDate dateValidation;

    @ManyToOne
    @JoinColumn(name = "admin_validateur_id")
    private Utilisateur adminValidateur;
} 