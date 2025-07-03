package itu.biblio.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import itu.biblio.projection.ReservatioProjection;

import itu.biblio.entities.Reservation;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
    
    @Query("SELECT r.id AS reservationId, u.nom AS utilisateurNom, u.prenom AS utilisateurPrenom, u.email AS utilisateurEmail, l.titre AS livreTitre, l.auteur AS livreAuteur, r.dateReservation AS dateReservation " +
           "FROM Reservation r " +
           "LEFT JOIN r.utilisateur u " +
           "LEFT JOIN r.livre l " +
           "WHERE r.estValidee = false " +
           "ORDER BY r.dateReservation DESC")
    List<ReservatioProjection> findAllReservationsWithDetails();
}