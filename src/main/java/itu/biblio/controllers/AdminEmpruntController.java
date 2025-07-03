package itu.biblio.controllers;

import itu.biblio.entities.*;
import itu.biblio.projection.EmpruntProjection;
import itu.biblio.projection.LivreDisponibiliteProjection;
import itu.biblio.projection.ListeLivreParAdherantProjection;
import itu.biblio.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/admin/emprunts")
public class AdminEmpruntController {
    
    @Autowired
    private EmpruntService empruntService;
    
    @Autowired
    private EmpruntDetailsService empruntDetailsService;
    
    @Autowired
    private LivreServices livreServices;
    
    @Autowired
    private UtilisateurServices utilisateurServices;
    
    @Autowired
    private TypeEmpruntService typeEmpruntService;
    
    @Autowired
    private AbonnementService abonnementService;

    // Liste de tous les emprunts
    @GetMapping
    public String listEmprunts(Model model) {
        // Détecter et mettre à jour les retards automatiquement
        empruntService.detecterEtMettreAJourRetards();
        
        List<EmpruntProjection> emprunts = empruntService.getAllEmpruntsWithDetails();
        long empruntsEnRetard = empruntService.countEmpruntsEnRetard();
        
        model.addAttribute("emprunts", emprunts);
        model.addAttribute("empruntsEnRetard", empruntsEnRetard);
        return "admin/emprunts/list";
    }

    // Formulaire de création d'emprunt
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        List<Utilisateur> utilisateurs = utilisateurServices.getAllUtilisateurs();
        List<TypeEmprunt> typeEmprunts = typeEmpruntService.getAllTypeEmprunts();
        
        model.addAttribute("utilisateurs", utilisateurs);
        model.addAttribute("typeEmprunts", typeEmprunts);
        model.addAttribute("emprunt", new Emprunt());
        return "admin/emprunts/create";
    }

    // Création d'un emprunt
    @PostMapping("/create")
    public String createEmprunt(@RequestParam Integer utilisateurId,
                               @RequestParam Integer livreId,
                               @RequestParam Integer typeEmpruntId,
                               @RequestParam String dateRetour,
                               RedirectAttributes redirectAttributes) {
        try {
            // Vérifier l'abonnement de l'utilisateur jusqu'à la date de retour
            LocalDate dateRetourParsed = LocalDate.parse(dateRetour);
            if (!abonnementService.canEmprunterUntil(utilisateurId, dateRetourParsed)) {
                String messageAbonnement = abonnementService.getMessageAbonnement(utilisateurId);
                redirectAttributes.addFlashAttribute("error", "L'utilisateur ne peut pas emprunter jusqu'au " + dateRetourParsed.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ": " + messageAbonnement);
                return "redirect:/admin/emprunts/create";
            }
            
            // Vérifier si l'utilisateur peut emprunter plus de livres
            if (!abonnementService.canEmprunterPlus(utilisateurId)) {
                String messageEmprunts = abonnementService.getMessageEmprunts(utilisateurId);
                redirectAttributes.addFlashAttribute("error", "L'utilisateur ne peut pas emprunter plus de livres: " + messageEmprunts);
                return "redirect:/admin/emprunts/create";
            }

            // Vérifier si l'utilisateur peut accéder au livre
            List<ListeLivreParAdherantProjection> livresAccessibles = livreServices.getAllLivres(utilisateurId);
            boolean livreAccessible = livresAccessibles.stream()
                .anyMatch(livre -> livre.getId().equals(livreId));
            
            if (!livreAccessible) {
                redirectAttributes.addFlashAttribute("error", "L'utilisateur n'a pas accès à ce livre");
                return "redirect:/admin/emprunts/create";
            }

            // Vérifier la disponibilité réelle du livre (emprunts + réservations)
            LivreDisponibiliteProjection disponibilite = livreServices.getLivreDisponibiliteReelleById(livreId);
            if (disponibilite == null || disponibilite.getExemplairesDisponibles() <= 0) {
                redirectAttributes.addFlashAttribute("error", "Le livre n'est pas disponible (tous les exemplaires sont empruntés ou réservés)");
                return "redirect:/admin/emprunts/create";
            }

            // Créer l'emprunt
            Emprunt emprunt = new Emprunt();
            Utilisateur utilisateur = new Utilisateur();
            utilisateur.setId(utilisateurId);
            emprunt.setUtilisateur(utilisateur);
            emprunt.setDateEmprunt(LocalDate.now());
            emprunt.setDateRetour(dateRetourParsed);
            emprunt.setStatutEmprunt("en_cours");
            
            Emprunt savedEmprunt = empruntService.saveEmprunt(emprunt);

            // Créer les détails de l'emprunt
            EmpruntDetail empruntDetail = new EmpruntDetail();
            empruntDetail.setEmprunt(savedEmprunt);
            Livre livre = new Livre();
            livre.setId(livreId);
            empruntDetail.setLivre(livre);
            empruntDetail.setDateDebut(LocalDate.now());
            empruntDetail.setDateFin(dateRetourParsed);
            empruntDetail.setDateRetour(null); // Sera mis à jour lors du retour
            
            empruntDetailsService.saveEmpruntDetails(empruntDetail);

            // Mettre à jour le statut du livre
            livreServices.updateLivreStatus(livreId, "en_cours_de_pret");

            redirectAttributes.addFlashAttribute("success", "Emprunt créé avec succès");
            return "redirect:/admin/emprunts";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la création de l'emprunt: " + e.getMessage());
            return "redirect:/admin/emprunts/create";
        }
    }

    // Détails d'un emprunt
    @GetMapping("/{id}")
    public String showEmpruntDetails(@PathVariable Integer id, Model model) {
        EmpruntProjection emprunt = empruntService.getEmpruntByIdWithDetails(id);
        if (emprunt == null) {
            return "redirect:/admin/emprunts?error=not_found";
        }
        model.addAttribute("emprunt", emprunt);
        return "admin/emprunts/details";
    }

    // Formulaire de modification d'emprunt
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Integer id, Model model) {
        Emprunt emprunt = empruntService.getEmpruntById(id).orElse(null);
        if (emprunt == null) {
            return "redirect:/admin/emprunts?error=not_found";
        }
        
        List<Utilisateur> utilisateurs = utilisateurServices.getAllUtilisateurs();
        List<Livre> livres = livreServices.getAllLivresForAdmin();
        List<TypeEmprunt> typeEmprunts = typeEmpruntService.getAllTypeEmprunts();
        
        model.addAttribute("emprunt", emprunt);
        model.addAttribute("utilisateurs", utilisateurs);
        model.addAttribute("livres", livres);
        model.addAttribute("typeEmprunts", typeEmprunts);
        return "admin/emprunts/edit";
    }

    // Modification d'un emprunt
    @PostMapping("/{id}/edit")
    public String updateEmprunt(@PathVariable Integer id,
                               @RequestParam Integer utilisateurId,
                               @RequestParam Integer livreId,
                               @RequestParam String dateRetour,
                               @RequestParam String statutEmprunt,
                               RedirectAttributes redirectAttributes) {
        try {
            Emprunt emprunt = empruntService.getEmpruntById(id).orElse(null);
            if (emprunt == null) {
                redirectAttributes.addFlashAttribute("error", "Emprunt non trouvé");
                return "redirect:/admin/emprunts";
            }

            // Vérifier si l'utilisateur peut accéder au livre
            List<ListeLivreParAdherantProjection> livresAccessibles = livreServices.getAllLivres(utilisateurId);
            boolean livreAccessible = livresAccessibles.stream()
                .anyMatch(livre -> livre.getId().equals(livreId));
            
            if (!livreAccessible) {
                redirectAttributes.addFlashAttribute("error", "L'utilisateur n'a pas accès à ce livre");
                return "redirect:/admin/emprunts/" + id + "/edit";
            }

            // Mettre à jour l'emprunt
            Utilisateur utilisateur = new Utilisateur();
            utilisateur.setId(utilisateurId);
            emprunt.setUtilisateur(utilisateur);
            emprunt.setDateRetour(LocalDate.parse(dateRetour));
            emprunt.setStatutEmprunt(statutEmprunt);
            
            empruntService.saveEmprunt(emprunt);

            redirectAttributes.addFlashAttribute("success", "Emprunt modifié avec succès");
            return "redirect:/admin/emprunts";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la modification: " + e.getMessage());
            return "redirect:/admin/emprunts/" + id + "/edit";
        }
    }

    // Retour d'un livre
    @PostMapping("/{id}/return")
    public String returnBook(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            Emprunt emprunt = empruntService.getEmpruntById(id).orElse(null);
            if (emprunt == null) {
                redirectAttributes.addFlashAttribute("error", "Emprunt non trouvé");
                return "redirect:/admin/emprunts";
            }

            // Mettre à jour le statut de l'emprunt
            emprunt.setStatutEmprunt("retourne");
            empruntService.saveEmprunt(emprunt);

            // Mettre à jour les détails de l'emprunt
            List<EmpruntDetail> details = empruntDetailsService.getEmpruntDetailsByEmpruntId(id);
            for (EmpruntDetail detail : details) {
                detail.setDateRetour(LocalDate.now());
                empruntDetailsService.saveEmpruntDetails(detail);
                
                // Mettre à jour le statut du livre
                livreServices.updateLivreStatus(detail.getLivre().getId(), "dispo");
            }

            redirectAttributes.addFlashAttribute("success", "Livre retourné avec succès");
            return "redirect:/admin/emprunts";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors du retour: " + e.getMessage());
            return "redirect:/admin/emprunts";
        }
    }

    // Retour d'un livre en retard
    @PostMapping("/{id}/return-retard")
    public String returnBookEnRetard(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            Emprunt emprunt = empruntService.getEmpruntById(id).orElse(null);
            if (emprunt == null) {
                redirectAttributes.addFlashAttribute("error", "Emprunt non trouvé");
                return "redirect:/admin/emprunts";
            }

            // Calculer les jours de retard
            int joursDeRetard = empruntService.getJoursDeRetard(id);

            // Mettre à jour le statut de l'emprunt
            emprunt.setStatutEmprunt("retourne");
            empruntService.saveEmprunt(emprunt);

            // Mettre à jour les détails de l'emprunt
            List<EmpruntDetail> details = empruntDetailsService.getEmpruntDetailsByEmpruntId(id);
            for (EmpruntDetail detail : details) {
                detail.setDateRetour(LocalDate.now());
                empruntDetailsService.saveEmpruntDetails(detail);
                
                // Mettre à jour le statut du livre
                livreServices.updateLivreStatus(detail.getLivre().getId(), "dispo");
            }

            String message = "Livre en retard retourné avec succès";
            if (joursDeRetard > 0) {
                message += " (Retard de " + joursDeRetard + " jour(s))";
            }
            
            redirectAttributes.addFlashAttribute("success", message);
            return "redirect:/admin/emprunts";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors du retour: " + e.getMessage());
            return "redirect:/admin/emprunts";
        }
    }

    // Suppression d'un emprunt
    @PostMapping("/{id}/delete")
    @Transactional
    public String deleteEmprunt(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            Emprunt emprunt = empruntService.getEmpruntById(id).orElse(null);
            if (emprunt == null) {
                redirectAttributes.addFlashAttribute("error", "Emprunt non trouvé");
                return "redirect:/admin/emprunts";
            }

            // Supprimer les détails de l'emprunt
            List<EmpruntDetail> details = empruntDetailsService.getEmpruntDetailsByEmpruntId(id);
            for (EmpruntDetail detail : details) {
                // Remettre le livre en statut disponible si l'emprunt était en cours
                if ("en_cours".equals(emprunt.getStatutEmprunt()) || "En cours".equals(emprunt.getStatutEmprunt())) {
                    livreServices.updateLivreStatus(detail.getLivre().getId(), "dispo");
                }
                // Supprimer le détail
                empruntDetailsService.deleteEmpruntDetails(detail.getId());
            }

            // Supprimer l'emprunt
            empruntService.deleteEmprunt(id);

            redirectAttributes.addFlashAttribute("success", "Emprunt supprimé avec succès");
            return "redirect:/admin/emprunts";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression: " + e.getMessage());
            return "redirect:/admin/emprunts";
        }
    }

    // API pour vérifier la disponibilité d'un livre
    @GetMapping("/check-availability/{livreId}")
    @ResponseBody
    public LivreDisponibiliteProjection checkAvailability(@PathVariable Integer livreId) {
        return livreServices.getLivreDisponibiliteReelleById(livreId);
    }

    // API pour obtenir les livres accessibles à un utilisateur
    @GetMapping("/user-books/{userId}")
    @ResponseBody
    public List<ListeLivreParAdherantProjection> getUserBooks(@PathVariable Integer userId) {
        return livreServices.getAllLivres(userId);
    }

    // API pour vérifier l'abonnement d'un utilisateur
    @GetMapping("/check-subscription/{userId}")
    @ResponseBody
    public AbonnementInfo checkSubscription(@PathVariable Integer userId) {
        boolean canEmprunter = abonnementService.canEmprunter(userId);
        String message = abonnementService.getMessageAbonnement(userId);
        
        return new AbonnementInfo(canEmprunter, message);
    }
    
    @GetMapping("/check-emprunts/{userId}")
    @ResponseBody
    public EmpruntsInfo checkEmprunts(@PathVariable Integer userId) {
        boolean canEmprunterPlus = abonnementService.canEmprunterPlus(userId);
        String message = abonnementService.getMessageEmprunts(userId);
        return new EmpruntsInfo(canEmprunterPlus, message);
    }

    // Classe interne pour l'API d'abonnement
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
    
    // Classe interne pour l'API d'emprunts
    public static class EmpruntsInfo {
        private boolean canEmprunterPlus;
        private String message;

        public EmpruntsInfo(boolean canEmprunterPlus, String message) {
            this.canEmprunterPlus = canEmprunterPlus;
            this.message = message;
        }

        public boolean isCanEmprunterPlus() {
            return canEmprunterPlus;
        }

        public void setCanEmprunterPlus(boolean canEmprunterPlus) {
            this.canEmprunterPlus = canEmprunterPlus;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
} 