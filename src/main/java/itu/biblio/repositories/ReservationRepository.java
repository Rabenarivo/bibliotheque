package itu.biblio.repositories;

import itu.biblio.entities.Reservation;
import itu.biblio.projection.ReservationProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
    

    
    @Query("SELECT r.id AS reservationId, u.nom AS utilisateurNom, u.prenom AS utilisateurPrenom, u.email AS utilisateurEmail, l.titre AS livreTitre, l.auteur AS livreAuteur, r.dateReservation AS dateReservation " +
           "FROM Reservation r " +
           "LEFT JOIN r.utilisateur u " +
           "LEFT JOIN r.livre l " +
           "WHERE r.estValidee = false " +
           "ORDER BY r.dateReservation DESC")
    List<ReservationProjection> findAllReservationsWithDetails();

    @Query("SELECT r.id as reservationId, u.nom as utilisateurNom, u.prenom as utilisateurPrenom, " +
           "u.email as utilisateurEmail, l.titre as livreTitre, l.auteur as livreAuteur, " +
           "r.dateReservation as dateReservation " +
           "FROM Reservation r " +
           "LEFT JOIN r.utilisateur u " +
           "LEFT JOIN r.livre l " +
           "WHERE r.id = :reservationId")
    Optional<ReservationProjection> findReservationById(@Param("reservationId") Integer reservationId);
}
