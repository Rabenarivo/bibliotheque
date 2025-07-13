package itu.biblio.controllers;

import itu.biblio.entities.DemandeProlongement;
import itu.biblio.entities.Emprunt;
import itu.biblio.services.DemandeProlongementService;
import itu.biblio.services.EmpruntService;
import itu.biblio.services.UtilisateurServices;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/prolongements")
public class DemandeProlongementUtilisateurController {
    
    @Autowired
    private DemandeProlongementService demandeProlongementService;
    
    @Autowired
    private EmpruntService empruntService;
    
    @Autowired
    private UtilisateurServices utilisateurServices;
    
    // Page d'accueil des prolongements
    @GetMapping
    public String index(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        
        // Récupérer les informations de l'utilisateur
        model.addAttribute("utilisateur", utilisateurServices.getUtilisateurById(userId).orElse(null));
        return "prolongements/index";
    }
    
    // Formulaire de demande de prolongement
    @GetMapping("/demander/{empruntId}")
    public String showDemandeForm(@PathVariable Integer empruntId, 
                                 HttpSession session, 
                                 Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        
        // Vérifier que l'emprunt peut être prolongé
        if (!demandeProlongementService.peutProlongerEmprunt(empruntId, userId)) {
            return "redirect:/profile?error=cannot_prolong";
        }
        
        // Vérifier que l'utilisateur peut faire une demande
        if (!demandeProlongementService.peutDemanderProlongement(userId)) {
            return "redirect:/profile?error=too_many_requests";
        }
        
        model.addAttribute("empruntId", empruntId);
        return "prolongements/demande";
    }
    
    // Soumettre une demande de prolongement
    @PostMapping("/demander/{empruntId}")
    public String creerDemande(@PathVariable Integer empruntId,
                              @RequestParam String nouvelleDate,
                              @RequestParam String motif,
                              @RequestParam String dateDemande,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        try {
            Integer userId = (Integer) session.getAttribute("userId");
            if (userId == null) {
                return "redirect:/login";
            }
            
            LocalDate date = LocalDate.parse(nouvelleDate);
            LocalDate dateDemandeParsed = LocalDate.parse(dateDemande);
            
            if (date.isBefore(dateDemandeParsed)) {
                redirectAttributes.addFlashAttribute("error", "La nouvelle date ne peut pas être antérieure à la date de demande");
                return "redirect:/prolongements/demander/" + empruntId;
            }
            
            DemandeProlongement demande = demandeProlongementService.creerDemande(
                empruntId, userId, date, motif, dateDemandeParsed);
            
            redirectAttributes.addFlashAttribute("success", "Demande de prolongement créée avec succès");
            return "redirect:/profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
            return "redirect:/prolongements/demander/" + empruntId;
        }
    }
    
    // Page de test des conditions de prolongement
    @GetMapping("/test/{empruntId}")
    public String testProlongement(@PathVariable Integer empruntId, 
                                  HttpSession session, 
                                  Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        
        var emprunt = empruntService.getEmpruntById(empruntId).orElse(null);
        var utilisateur = utilisateurServices.getUtilisateurById(userId).orElse(null);
        var demandes = demandeProlongementService.getDemandesByUtilisateur(userId);
        
        long demandesEnAttente = demandes.stream()
            .filter(d -> "en_attente".equals(d.getStatut()))
            .count();
        
        boolean peutProlonger = demandeProlongementService.peutDemanderProlongement(userId);
        boolean peutProlongerEmprunt = demandeProlongementService.peutProlongerEmprunt(empruntId, userId);
        
        model.addAttribute("userId", userId);
        model.addAttribute("empruntId", empruntId);
        model.addAttribute("utilisateur", utilisateur);
        model.addAttribute("emprunt", emprunt);
        model.addAttribute("demandes", demandes);
        model.addAttribute("demandesEnAttente", demandesEnAttente);
        model.addAttribute("peutProlonger", peutProlonger);
        model.addAttribute("peutProlongerEmprunt", peutProlongerEmprunt);
        
        return "prolongements/test";
    }
    
    // Page des demandes de l'utilisateur
    @GetMapping("/mes-demandes")
    public String mesDemandes(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        
        List<DemandeProlongement> allDemandes = demandeProlongementService.getDemandesByUtilisateur(userId);
        
        long demandesEnAttente = allDemandes.stream()
            .filter(d -> "en_attente".equals(d.getStatut()))
            .count();
        long demandesAcceptees = allDemandes.stream()
            .filter(d -> "acceptee".equals(d.getStatut()))
            .count();
        long demandesRefusees = allDemandes.stream()
            .filter(d -> "refusee".equals(d.getStatut()))
            .count();
        
        model.addAttribute("demandes", allDemandes);
        model.addAttribute("demandesEnAttente", demandesEnAttente);
        model.addAttribute("demandesAcceptees", demandesAcceptees);
        model.addAttribute("demandesRefusees", demandesRefusees);
        return "prolongements/mes-demandes";
    }
} 