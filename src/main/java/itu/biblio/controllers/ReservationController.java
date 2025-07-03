package itu.biblio.controllers;

import itu.biblio.entities.Livre;
import itu.biblio.entities.Reservation;
import itu.biblio.entities.Utilisateur;
import itu.biblio.projection.ListeLivreParAdherantProjection;
import itu.biblio.projection.LivreDisponibiliteProjection;
import itu.biblio.services.LivreServices;
import itu.biblio.services.ReservationService;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
public class ReservationController {
    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/reservation_livre")
    public String showallreservation(Model model) {
        List<Reservation> reservations = reservationService.getAllReservations();
        model.addAttribute("reservations", reservations);
        return "/reservation";

    }
    
    
}
