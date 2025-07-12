package itu.biblio.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "penalite")
public class Penalite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "emprunt_id")
    private Emprunt emprunt;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;

    @Column(name = "sanction")
    private Integer sanction;

    @Column(name = "nb_jour_sanction")
    private Integer nbJourSanction;

    @Column(name = "date_debut")
    private LocalDate dateDebut;
}