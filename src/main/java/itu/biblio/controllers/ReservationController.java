package itu.biblio.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import itu.biblio.services.ReservationService;
import itu.biblio.services.TypeEmpruntService;
import itu.biblio.projection.ReservationProjection;

@Controller
@RequestMapping("/admin/reservations")
public class ReservationController {
    @Autowired
    private ReservationService reservationService;
    
    @Autowired
    private TypeEmpruntService typeEmpruntService;

    @GetMapping
    public String showReservations(Model model) {
        try {
            model.addAttribute("reservations", reservationService.getPendingReservations());
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors du chargement des réservations: " + e.getMessage());
        }
        return "resa";
    }

    @GetMapping("/validate-form")
    public String showValidationForm(@RequestParam Integer reservationId, Model model) {
        // Récupérer les détails de la réservation
        ReservationProjection reservation = reservationService.getReservationById(reservationId);
        model.addAttribute("reservation", reservation);
        model.addAttribute("typeEmprunts", typeEmpruntService.getAllTypeEmprunts());
        return "validation-form";
    }

    @PostMapping("/validate")
    public String validateReservation(@RequestParam Integer reservationId,
                                    @RequestParam Integer typeEmpruntId,
                                    @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") String dateRetour,
                                    Model model) {
        try {
            // Validation de la date
            LocalDate dateRetourLD = LocalDate.parse(dateRetour);
            if (dateRetourLD.isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("La date de retour ne peut pas être dans le passé");
            }
            
            reservationService.validateReservationAndCreateEmprunt(reservationId, typeEmpruntId, dateRetour);
            return "redirect:/admin/reservations?success=true";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/admin/reservations?error=true";
        }
    }
}