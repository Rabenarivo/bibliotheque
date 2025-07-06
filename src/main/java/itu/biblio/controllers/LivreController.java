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


import java.time.LocalDate;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class LivreController {
    private final LivreServices livreService;
    private final ReservationService reservationService;

    public LivreController(LivreServices livreService, ReservationService ReservationService ) {
        this.livreService = livreService;
        this.reservationService = ReservationService;
    }

   

    @GetMapping("/livres")
    public List<ListeLivreParAdherantProjection> getAllLivres(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        List<ListeLivreParAdherantProjection> livres = livreService.getAllLivres(userId);
        model.addAttribute("livres", livres);
        return livres;
    }

    @GetMapping("/reservation_livre")
    public String reserverLivre(@RequestParam("livreId") Integer livreId, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
    
        // Check availability of the book
        LivreDisponibiliteProjection disponibilite = livreService.getLivreDisponibiliteById(livreId);
        if (disponibilite == null || disponibilite.getExemplairesDisponibles() <= 0) {
            return "redirect:/livres?error=not_available";
        }
    
        // Proceed with the reservation
        Reservation reservation = new Reservation();
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(userId);
        reservation.setUtilisateur(utilisateur);
    
        Livre livre = new Livre();
        livre.setId(livreId);
        reservation.setLivre(livre);
    
        reservation.setDateDebut(LocalDate.now());
        reservation.setDateReservation(LocalDate.now());
        reservation.setStatutReservation(false);
        reservation.setEstValidee(false);
        reservationService.saveReservation(reservation);
    
        return "redirect:/livres?success";
    }
    

}
