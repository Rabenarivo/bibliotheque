package itu.biblio.controllers;

import itu.biblio.entities.Abonnement;
import itu.biblio.entities.Utilisateur;
import itu.biblio.services.AbonnementService;
import itu.biblio.services.UtilisateurServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin/abonnements")
public class AdminAbonnementController {
    
    @Autowired
    private AbonnementService abonnementService;
    
    @Autowired
    private UtilisateurServices utilisateurServices;

    // Liste de tous les abonnements
    @GetMapping
    public String listAbonnements(Model model) {
        List<Abonnement> abonnements = abonnementService.getAllAbonnements();
        model.addAttribute("abonnements", abonnements);
        return "admin/abonnements/list";
    }

    // Formulaire de création d'abonnement
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        List<Utilisateur> utilisateurs = utilisateurServices.getAllUtilisateurs();
        model.addAttribute("utilisateurs", utilisateurs);
        model.addAttribute("abonnement", new Abonnement());
        return "admin/abonnements/create";
    }

    // Création d'un abonnement
    @PostMapping("/create")
    public String createAbonnement(@RequestParam Integer utilisateurId,
                                  @RequestParam String dateDebut,
                                  @RequestParam String dateFin,
                                  RedirectAttributes redirectAttributes) {
        try {
            Abonnement abonnement = new Abonnement();
            Utilisateur utilisateur = new Utilisateur();
            utilisateur.setId(utilisateurId);
            abonnement.setUtilisateur(utilisateur);
            abonnement.setDateDebut(LocalDate.parse(dateDebut));
            abonnement.setDateFin(LocalDate.parse(dateFin));
            
            abonnementService.saveAbonnement(abonnement);

            redirectAttributes.addFlashAttribute("success", "Abonnement créé avec succès");
            return "redirect:/admin/abonnements";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la création de l'abonnement: " + e.getMessage());
            return "redirect:/admin/abonnements/create";
        }
    }

    // Détails d'un abonnement
    @GetMapping("/{id}")
    public String showAbonnementDetails(@PathVariable Integer id, Model model) {
        Abonnement abonnement = abonnementService.getAbonnementById(id).orElse(null);
        if (abonnement == null) {
            return "redirect:/admin/abonnements?error=not_found";
        }
        model.addAttribute("abonnement", abonnement);
        return "admin/abonnements/details";
    }

    // Formulaire de modification d'abonnement
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Integer id, Model model) {
        Abonnement abonnement = abonnementService.getAbonnementById(id).orElse(null);
        if (abonnement == null) {
            return "redirect:/admin/abonnements?error=not_found";
        }
        
        List<Utilisateur> utilisateurs = utilisateurServices.getAllUtilisateurs();
        model.addAttribute("abonnement", abonnement);
        model.addAttribute("utilisateurs", utilisateurs);
        return "admin/abonnements/edit";
    }

    // Modification d'un abonnement
    @PostMapping("/{id}/edit")
    public String updateAbonnement(@PathVariable Integer id,
                                  @RequestParam Integer utilisateurId,
                                  @RequestParam String dateDebut,
                                  @RequestParam String dateFin,
                                  RedirectAttributes redirectAttributes) {
        try {
            Abonnement abonnement = abonnementService.getAbonnementById(id).orElse(null);
            if (abonnement == null) {
                redirectAttributes.addFlashAttribute("error", "Abonnement non trouvé");
                return "redirect:/admin/abonnements";
            }

            Utilisateur utilisateur = new Utilisateur();
            utilisateur.setId(utilisateurId);
            abonnement.setUtilisateur(utilisateur);
            abonnement.setDateDebut(LocalDate.parse(dateDebut));
            abonnement.setDateFin(LocalDate.parse(dateFin));
            
            abonnementService.saveAbonnement(abonnement);

            redirectAttributes.addFlashAttribute("success", "Abonnement modifié avec succès");
            return "redirect:/admin/abonnements";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la modification: " + e.getMessage());
            return "redirect:/admin/abonnements/" + id + "/edit";
        }
    }

    // Suppression d'un abonnement
    @PostMapping("/{id}/delete")
    public String deleteAbonnement(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            Abonnement abonnement = abonnementService.getAbonnementById(id).orElse(null);
            if (abonnement == null) {
                redirectAttributes.addFlashAttribute("error", "Abonnement non trouvé");
                return "redirect:/admin/abonnements";
            }

            abonnementService.deleteAbonnement(id);

            redirectAttributes.addFlashAttribute("success", "Abonnement supprimé avec succès");
            return "redirect:/admin/abonnements";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression: " + e.getMessage());
            return "redirect:/admin/abonnements";
        }
    }

    // API pour vérifier l'abonnement d'un utilisateur
    @GetMapping("/check/{userId}")
    @ResponseBody
    public AbonnementInfo checkAbonnement(@PathVariable Integer userId) {
        boolean canEmprunter = abonnementService.canEmprunter(userId);
        String message = abonnementService.getMessageAbonnement(userId);
        
        return new AbonnementInfo(canEmprunter, message);
    }

    // Classe interne pour l'API
    public static class AbonnementInfo {
        private boolean canEmprunter;
        private String message;

        public AbonnementInfo(boolean canEmprunter, String message) {
            this.canEmprunter = canEmprunter;
            this.message = message;
        }

        public boolean isCanEmprunter() {
            return canEmprunter;
        }

        public void setCanEmprunter(boolean canEmprunter) {
            this.canEmprunter = canEmprunter;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
} 