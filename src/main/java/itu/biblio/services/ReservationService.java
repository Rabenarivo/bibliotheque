package itu.biblio.services;

import itu.biblio.entities.Reservation;
import itu.biblio.repositories.ReservationRepository;
import org.springframework.stereotype.Service;

@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    // Save a reservation
    public void saveReservation(Reservation reservation) {
        reservationRepository.save(reservation);
    }
}