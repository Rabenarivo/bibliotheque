package itu.biblio.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "reservation")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "livre_id")
    private Livre livre;

    @Column(name = "date_reservation")
    private LocalDateTime dateReservation;

    @Column(name = "statut")
    private String statut;  // PENDING, CONFIRMED, CANCELLED

    @Column(name = "date_debut")
    private LocalDate dateDebut;
}