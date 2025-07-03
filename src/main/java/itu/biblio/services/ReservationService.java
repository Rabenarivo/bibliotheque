package itu.biblio.services;

import itu.biblio.entities.Reservation;
import itu.biblio.entities.Emprunt;
import itu.biblio.entities.EmpruntDetails;
import itu.biblio.entities.HistoriqueLivre;
import itu.biblio.entities.StatutLivre;
import itu.biblio.projection.ReservationProjection;
import itu.biblio.repositories.ReservationRepository;
import itu.biblio.repositories.EmpruntRepository;
import itu.biblio.repositories.EmpruntDetailsRepository;
import itu.biblio.repositories.HistoriqueLivreRepository;
import itu.biblio.repositories.StatutLivreRepository;
import itu.biblio.repositories.UtilisateurRepository;
import itu.biblio.repositories.LivreRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.qos.logback.core.model.Model;

@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final EmpruntRepository empruntRepository;
    private final EmpruntDetailsRepository empruntDetailsRepository;
    private final HistoriqueLivreRepository historiqueLivreRepository;
    private final StatutLivreRepository statutLivreRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final LivreRepository livreRepository;

    public ReservationService(ReservationRepository reservationRepository,
                            EmpruntRepository empruntRepository,
                            EmpruntDetailsRepository empruntDetailsRepository,
                            HistoriqueLivreRepository historiqueLivreRepository,
                            StatutLivreRepository statutLivreRepository,
                            UtilisateurRepository utilisateurRepository,
                            LivreRepository livreRepository) {
        this.reservationRepository = reservationRepository;
        this.empruntRepository = empruntRepository;
        this.empruntDetailsRepository = empruntDetailsRepository;
        this.historiqueLivreRepository = historiqueLivreRepository;
        this.statutLivreRepository = statutLivreRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.livreRepository = livreRepository;
    }

    // Save a reservation
    public void saveReservation(Reservation reservation) {
        reservationRepository.save(reservation);
    }

    public List<ReservationProjection> getAllReservationsWithDetails(){
        return reservationRepository.findAllReservationsWithDetails();
    }

    public void validateReservation(Integer reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid reservation ID"));
        reservation.setEstValidee(true);
        reservationRepository.save(reservation);
    }

    public List<ReservationProjection> getPendingReservations() {
        return reservationRepository.findAllReservationsWithDetails()
            .stream()
            .collect(java.util.stream.Collectors.toList());
    }

    public ReservationProjection getReservationById(Integer reservationId) {
        return reservationRepository.findReservationById(reservationId)
            .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));
    }

    @Transactional
    public void validateReservationAndCreateEmprunt(Integer reservationId, Integer typeEmpruntId, String dateRetour) {
        // 1. Récupérer la réservation
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid reservation ID"));

        // 2. Créer l'emprunt
        Emprunt emprunt = new Emprunt();
        emprunt.setUtilisateur(reservation.getUtilisateur());
        emprunt.setDateEmprunt(LocalDate.now());
        emprunt.setDateRetour(LocalDate.parse(dateRetour, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        emprunt.setStatutEmprunt("En cours");
        emprunt = empruntRepository.save(emprunt);

        // 3. Créer les détails d'emprunt
        EmpruntDetails empruntDetails = new EmpruntDetails();
        empruntDetails.setEmprunt(emprunt);
        empruntDetails.setLivre(reservation.getLivre());
        empruntDetails.setTypeEmpruntId(typeEmpruntId);
        empruntDetailsRepository.save(empruntDetails);

        // 4. Mettre à jour l'historique du livre
        HistoriqueLivre historiqueLivre = new HistoriqueLivre();
        historiqueLivre.setLivre(reservation.getLivre());
        historiqueLivre.setUtilisateur(reservation.getUtilisateur());
        historiqueLivre.setDateAction(LocalDate.now());
        historiqueLivre.setTypeAction("Emprunt");
        historiqueLivre.setDescription("Livre emprunté via validation de réservation");
        historiqueLivreRepository.save(historiqueLivre);

        // 5. Mettre à jour le statut du livre (statut_id = 2 pour "Emprunté")
        StatutLivre statutLivre = statutLivreRepository.findById(2)
            .orElseThrow(() -> new IllegalArgumentException("Statut 'Emprunté' not found"));
        reservation.getLivre().setStatutLivre(statutLivre);
        livreRepository.save(reservation.getLivre());

        // 6. Valider la réservation
        reservation.setEstValidee(true);
        reservationRepository.save(reservation);
    }
}