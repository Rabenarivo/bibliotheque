package itu.biblio.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "adherant")
public class Adherant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "type", length = 100)
    private String type;

    @Column(name = "nbr_reservation")
    private Integer nbrReservation;

    @Column(name = "nbr_livre_pret")
    private Integer nbrLivrePret;

    @Column(name = "nbr_jrs_pret")
    private Integer nbrJrsPret;

}