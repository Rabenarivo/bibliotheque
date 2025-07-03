package itu.biblio.controllers;

import itu.biblio.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {
    
    @Autowired
    private EmpruntService empruntService;
    
    @Autowired
    private ReservationService reservationService;
    
    @Autowired
    private UtilisateurServices utilisateurServices;

    @GetMapping("/dashboard")
    public String showDashboard(Model model, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        
        // Récupérer les statistiques
        long empruntsEnCours = empruntService.countEmpruntsByStatut("en_cours");
        long reservationsEnAttente = reservationService.countPendingReservations();
        long empruntsRetard = empruntService.countEmpruntsEnRetard();
        long totalUtilisateurs = utilisateurServices.countAllUtilisateurs();
        
        model.addAttribute("empruntsEnCours", empruntsEnCours);
        model.addAttribute("reservationsEnAttente", reservationsEnAttente);
        model.addAttribute("empruntsRetard", empruntsRetard);
        model.addAttribute("totalUtilisateurs", totalUtilisateurs);
        
        // Récupérer les informations de l'utilisateur connecté
        utilisateurServices.getUtilisateurById(userId).ifPresent(user -> {
            model.addAttribute("utilisateur", user);
        });
        
        return "admin/dashboard";
    }
} 