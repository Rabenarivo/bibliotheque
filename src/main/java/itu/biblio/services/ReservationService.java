package itu.biblio.services;

import itu.biblio.entities.Reservation;
import itu.biblio.projection.ReservatioProjection;
import itu.biblio.repositories.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;

    @Autowired
    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public List<ReservatioProjection> getAllReservationsWithDetails() {
        return reservationRepository.findAllReservationsWithDetails();
    }

    public Reservation saveReservation(Reservation reservation) {
        return reservationRepository.save(reservation);
    }
}