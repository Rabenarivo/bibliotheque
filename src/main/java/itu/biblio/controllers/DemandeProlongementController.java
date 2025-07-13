package itu.biblio.controllers;

import itu.biblio.entities.DemandeProlongement;
import itu.biblio.services.DemandeProlongementService;
import itu.biblio.services.UtilisateurServices;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/prolongements")
public class DemandeProlongementController {
    
    @Autowired
    private DemandeProlongementService demandeProlongementService;
    
    @Autowired
    private UtilisateurServices utilisateurServices;
    
    // Liste de toutes les demandes de prolongement
    @GetMapping
    public String listDemandes(Model model, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        
        var utilisateur = utilisateurServices.getUtilisateurById(userId).orElse(null);
        if (utilisateur == null || !utilisateur.isAdmin()) {
            return "redirect:/profile";
        }
        
        List<DemandeProlongement> demandes = demandeProlongementService.getAllDemandesWithDetails();
        List<DemandeProlongement> demandesEnAttente = demandeProlongementService.getDemandesEnAttente();
        
        model.addAttribute("demandes", demandes);
        model.addAttribute("demandesEnAttente", demandesEnAttente);
        model.addAttribute("utilisateur", utilisateur);
        
        return "admin/prolongements";
    }
    
    // Approuver une demande de prolongement
    @PostMapping("/{id}/approuver")
    public String approuverDemande(@PathVariable Integer id, 
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        try {
            Integer adminId = (Integer) session.getAttribute("userId");
            if (adminId == null) {
                return "redirect:/login";
            }
            
            demandeProlongementService.approuverDemande(id, adminId);
            redirectAttributes.addFlashAttribute("success", "Demande de prolongement approuvée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'approbation: " + e.getMessage());
        }
        
        return "redirect:/admin/prolongements";
    }
    
    // Refuser une demande de prolongement
    @PostMapping("/{id}/refuser")
    public String refuserDemande(@PathVariable Integer id,
                                @RequestParam String motifRefus,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        try {
            Integer adminId = (Integer) session.getAttribute("userId");
            if (adminId == null) {
                return "redirect:/login";
            }
            
            demandeProlongementService.refuserDemande(id, adminId, motifRefus);
            redirectAttributes.addFlashAttribute("success", "Demande de prolongement refusée");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors du refus: " + e.getMessage());
        }
        
        return "redirect:/admin/prolongements";
    }
    
    // Détails d'une demande de prolongement
    @GetMapping("/{id}/details")
    public String showDemandeDetails(@PathVariable Integer id, 
                                   Model model, 
                                   HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        
        var utilisateur = utilisateurServices.getUtilisateurById(userId).orElse(null);
        if (utilisateur == null || !utilisateur.isAdmin()) {
            return "redirect:/profile";
        }
        
        var demande = demandeProlongementService.getDemandeById(id).orElse(null);
        if (demande == null) {
            return "redirect:/admin/prolongements?error=not_found";
        }
        
        model.addAttribute("demande", demande);
        model.addAttribute("utilisateur", utilisateur);
        
        return "admin/prolongement-details";
    }
} 