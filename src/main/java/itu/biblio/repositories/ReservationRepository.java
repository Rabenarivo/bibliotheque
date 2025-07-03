package itu.biblio.repositories;

import itu.biblio.entities.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
    
    @Query("SELECT r FROM Reservation r " +
           "LEFT JOIN FETCH r.utilisateur u " +
           "LEFT JOIN FETCH r.livre l " +
           "ORDER BY r.dateReservation DESC")
    List<Reservation> findAllReservationsWithDetails();
}