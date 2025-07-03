package itu.biblio.controllers;

import itu.biblio.entities.Livre;
import itu.biblio.entities.Reservation;
import itu.biblio.entities.Utilisateur;
import itu.biblio.projection.ListeLivreParAdherantProjection;
import itu.biblio.services.LivreDisponibiliteProjection;
import itu.biblio.services.LivreServices;
import itu.biblio.services.ReservationService;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
public class LivreController {

    private final LivreServices livreService;
    private final ReservationService reservationService;

    public LivreController(LivreServices livreService, ReservationService reservationService) {
        this.livreService = livreService;
        this.reservationService = reservationService;
    }

    @GetMapping("/livres")
    public String getAllLivres(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");

        List<ListeLivreParAdherantProjection> livres = livreService.getLivresByUserId(userId);
        model.addAttribute("livres", livres);

        return "livres"; // Thymeleaf template: livres.html
    }

    @GetMapping("/reservation_livre")
    public String reserverLivre(@RequestParam("livreId") Integer livreId, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        LivreDisponibiliteProjection disponibilite = livreService.getAvailableLivres(livreId);
        if (disponibilite == null || disponibilite.getExemplairesDisponibles() <= 0) {
            return "redirect:/livres?error=not_available";
        }

        Reservation reservation = new Reservation();
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(userId);
        reservation.setUtilisateur(utilisateur);

        Livre livre = new Livre();
        livre.setId(livreId);
        reservation.setLivre(livre);

        reservation.setDateDebut(LocalDate.now());

        reservationService.saveReservation(reservation);

        return "redirect:/livres?success=true";
    }
}
