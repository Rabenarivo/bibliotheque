package itu.biblio.services;

import itu.biblio.entities.Reservation;
import itu.biblio.repositories.ReservationRepository;
import itu.biblio.projection.ReservatioProjection;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ReservationService {
    private final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public void saveReservation(Reservation reservation) {
        reservationRepository.save(reservation);
    }

    public Optional<Reservation> getReservationById(Integer id) {
        return reservationRepository.findById(id);
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }
    public ReservatioProjection getallreservation() {
        List<Reservation> reservations = reservationRepository.findAllReservationsWithDetails();
        return (ReservatioProjection) reservations.get(0);
    }
    
    public List<Reservation> getAllReservationsWithDetails() {
        return reservationRepository.findAllReservationsWithDetails();
    }
    
    public Reservation save(Reservation reservation) {
        return reservationRepository.save(reservation);
    }
}