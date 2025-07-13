package itu.biblio.controllers;

import itu.biblio.services.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {
    
    @Autowired
    private EmpruntService empruntService;
    
    @Autowired
    private ReservationService reservationService;
    

    
    @Autowired
    private UtilisateurServices utilisateurServices;
    
    @Autowired
    private DemandeProlongementService demandeProlongementService;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/dashboard")
    public String showDashboard(Model model, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        
        var utilisateur = utilisateurServices.getUtilisateurById(userId).orElse(null);
        if (utilisateur == null || !utilisateur.isAdmin()) {
            return "redirect:/profile";
        }
        
        long empruntsEnCours = empruntService.countEmpruntsByStatut("en_cours");
        long reservationsEnAttente = reservationService.countPendingReservations();
        long empruntsEnRetard = empruntService.countEmpruntsEnRetard();
        long prolongementsEnAttente = demandeProlongementService.getDemandesEnAttente().size();

        long totalUtilisateurs = utilisateurServices.getAllUtilisateurs().size();
        
        model.addAttribute("empruntsEnCours", empruntsEnCours);
        model.addAttribute("reservationsEnAttente", reservationsEnAttente);
        model.addAttribute("empruntsEnRetard", empruntsEnRetard);
        model.addAttribute("prolongementsEnAttente", prolongementsEnAttente);

        model.addAttribute("totalUtilisateurs", totalUtilisateurs);
        model.addAttribute("utilisateur", utilisateur);
        
        return "admin/dashboard";
    }
    
    @PostMapping("/fix-quota")
    public String fixQuota(RedirectAttributes redirectAttributes) {
        try {
            // Corriger le quota des utilisateurs
            String updateSql = "UPDATE utilisateur SET quota_actuel = (SELECT a.nbr_livre_pret FROM adherant a WHERE a.id = utilisateur.id_adherant) WHERE quota_actuel IS NULL AND id_adherant IS NOT NULL";
            int updatedRows = jdbcTemplate.update(updateSql);
            
            redirectAttributes.addFlashAttribute("success", "Quota corrigé pour " + updatedRows + " utilisateur(s)");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la correction du quota: " + e.getMessage());
        }
        
        return "redirect:/admin/dashboard";
    }
} 