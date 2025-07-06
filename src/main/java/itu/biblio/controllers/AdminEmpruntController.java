package itu.biblio.controllers;

import itu.biblio.entities.*;
import itu.biblio.projection.EmpruntProjection;
import itu.biblio.projection.LivreDisponibiliteProjection;
import itu.biblio.projection.ListeLivreParAdherantProjection;
import itu.biblio.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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

    @Autowired
    private PenaliteService penaliteService;

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
        try {
            List<Utilisateur> utilisateurs = utilisateurServices.getAllUtilisateurs();
            List<TypeEmprunt> typeEmprunts = typeEmpruntService.getAllTypeEmprunts();
            Map<Integer, Integer> penalites = new java.util.HashMap<>();
            LocalDate now = java.time.LocalDate.now();
            for (Utilisateur u : utilisateurs) {
                List<Penalite> userPenalites = penaliteService.getPenalitesByUtilisateur(u.getId());
                for (Penalite p : userPenalites) {
                    java.time.LocalDate fin = p.getDateDebut().plusDays(p.getNbJourSanction());
                    if (now.isBefore(fin)) {
                        int joursRestants = (int) java.time.temporal.ChronoUnit.DAYS.between(now, fin);
                        penalites.put(u.getId(), joursRestants);
                    }
                }
            }
            model.addAttribute("utilisateurs", utilisateurs);
            model.addAttribute("typeEmprunts", typeEmprunts);
            model.addAttribute("emprunt", new Emprunt());
            model.addAttribute("penalites", penalites);
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors du chargement du formulaire d'emprunt : " + e.getMessage());
            model.addAttribute("utilisateurs", java.util.Collections.emptyList());
            model.addAttribute("typeEmprunts", java.util.Collections.emptyList());
            model.addAttribute("emprunt", new Emprunt());
            model.addAttribute("penalites", new java.util.HashMap<>());
        }
        return "admin/emprunts/create";
    }

    // Création d'un emprunt
    @PostMapping("/create")
    public String createEmprunt(@RequestParam Integer utilisateurId,
                               @RequestParam Integer livreId,
                               @RequestParam Integer typeEmpruntId,
                               @RequestParam String dateEmprunt,
                               @RequestParam String dateRetour,
                               RedirectAttributes redirectAttributes) {
        try {
            // Vérifier l'abonnement de l'utilisateur jusqu'à la date de retour
            LocalDate dateEmpruntParsed = LocalDate.parse(dateEmprunt);
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
            // Vérifier la pénalité côté serveur
            List<itu.biblio.entities.Penalite> userPenalites = penaliteService.getPenalitesByUtilisateur(utilisateurId);
            LocalDate now = dateEmpruntParsed;
            for (itu.biblio.entities.Penalite p : userPenalites) {
                LocalDate fin = p.getDateDebut().plusDays(p.getNbJourSanction());
                if (now.isBefore(fin)) {
                    int joursRestants = (int) java.time.temporal.ChronoUnit.DAYS.between(now, fin);
                    redirectAttributes.addFlashAttribute("error", "L'utilisateur est sous pénalité et ne peut pas emprunter pendant encore " + joursRestants + " jour(s).");
                    return "redirect:/admin/emprunts/create";
                }
            }
            // Créer l'emprunt
            Emprunt emprunt = new Emprunt();
            Utilisateur utilisateur = new Utilisateur();
            utilisateur.setId(utilisateurId);
            emprunt.setUtilisateur(utilisateur);
            emprunt.setDateEmprunt(dateEmpruntParsed);
            emprunt.setDateRetour(dateRetourParsed);
            emprunt.setStatutEmprunt("en_cours");
            Emprunt savedEmprunt = empruntService.saveEmprunt(emprunt);
            // Créer les détails de l'emprunt
            EmpruntDetail empruntDetail = new EmpruntDetail();
            empruntDetail.setEmprunt(savedEmprunt);
            Livre livre = new Livre();
            livre.setId(livreId);
            empruntDetail.setLivre(livre);
            empruntDetail.setDateDebut(dateEmpruntParsed);
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
    public String returnBook(@PathVariable Integer id, @RequestParam("dateRetour") String dateRetourStr, RedirectAttributes redirectAttributes) {
        try {
            Emprunt emprunt = empruntService.getEmpruntById(id).orElse(null);
            if (emprunt == null) {
                redirectAttributes.addFlashAttribute("error", "Emprunt non trouvé");
                return "redirect:/admin/emprunts";
            }

            LocalDate dateRetour = LocalDate.parse(dateRetourStr);
            // Mettre à jour le statut de l'emprunt
            emprunt.setStatutEmprunt("retourne");
            empruntService.saveEmprunt(emprunt);

            // Mettre à jour les détails de l'emprunt et appliquer la pénalité par livre en retard
            List<EmpruntDetail> details = empruntDetailsService.getEmpruntDetailsByEmpruntId(id);
            int totalPenaliteJours = 0;
            int totalLivresRetard = 0;
            for (EmpruntDetail detail : details) {
                detail.setDateRetour(dateRetour);
                empruntDetailsService.saveEmpruntDetails(detail);
                // Vérifier le retard pour chaque livre
                if (detail.getDateFin() != null && dateRetour.isAfter(detail.getDateFin())) {
                    penaliteService.applyPenalty(emprunt.getUtilisateur().getId(), emprunt.getId(), 5);
                    totalPenaliteJours += 5;
                    totalLivresRetard++;
                    // Mettre à jour le statut du livre à 'retard' (id=3)
                    livreServices.updateLivreStatusById(detail.getLivre().getId(), 3);
                } else {
                    // Mettre à jour le statut du livre normalement
                    livreServices.updateLivreStatus(detail.getLivre().getId(), "dispo");
                }
            }

            String message = "Livre retourné avec succès";
            if (totalLivresRetard > 0) {
                message += " (" + totalLivresRetard + " livre(s) en retard, pénalité totale : " + totalPenaliteJours + " jour(s))";
            }
            redirectAttributes.addFlashAttribute("success", message);
            return "redirect:/admin/emprunts";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors du retour: " + e.getMessage());
            return "redirect:/admin/emprunts";
        }
    }

    // Retour d'un livre en retard
    @PostMapping("/{id}/return-retard")
    public String returnBookEnRetard(@PathVariable Integer id, @RequestParam("dateRetour") String dateRetourStr, RedirectAttributes redirectAttributes) {
        try {
            Emprunt emprunt = empruntService.getEmpruntById(id).orElse(null);
            if (emprunt == null) {
                redirectAttributes.addFlashAttribute("error", "Emprunt non trouvé");
                return "redirect:/admin/emprunts";
            }

            LocalDate dateRetour = LocalDate.parse(dateRetourStr);

            // Calculer les jours de retard
            int joursDeRetard = empruntService.getJoursDeRetard(id);

            // Mettre à jour le statut de l'emprunt
            emprunt.setStatutEmprunt("retourne");
            empruntService.saveEmprunt(emprunt);

            // Mettre à jour les détails de l'emprunt
            List<EmpruntDetail> details = empruntDetailsService.getEmpruntDetailsByEmpruntId(id);
            for (EmpruntDetail detail : details) {
                detail.setDateRetour(dateRetour);
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

            // Supprimer les pénalités associées à cet emprunt
            List<Penalite> penalites = penaliteService.getPenalitesByEmprunt(id);
            for (Penalite penalite : penalites) {
                penaliteService.deletePenalite(penalite.getId());
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
    public ResponseEntity<?> checkAvailability(@PathVariable Integer livreId) {
        try {
            LivreDisponibiliteProjection projection = livreServices.getLivreDisponibiliteReelleById(livreId);
            if (projection == null) {
                return ResponseEntity.status(404).body(java.util.Collections.singletonMap("message", "Livre non trouvé"));
            }
            return ResponseEntity.ok(projection);
        } catch (Exception e) {
            e.printStackTrace(); // ou log.error(...)
            return ResponseEntity.status(500).body(java.util.Collections.singletonMap("message", "Erreur lors de la vérification de disponibilité"));
        }
    }

    // API pour obtenir les livres accessibles à un utilisateur
    @GetMapping("/user-books/{userId}")
    @ResponseBody
    public ResponseEntity<?> getUserBooks(@PathVariable Integer userId) {
        try {
            List<ListeLivreParAdherantProjection> livres = livreServices.getAllLivres(userId);
            return ResponseEntity.ok(livres);
        } catch (Exception e) {
            e.printStackTrace(); // ou log.error(...)
            return ResponseEntity.status(500).body(java.util.Collections.singletonMap("message", "Erreur lors du chargement des livres"));
        }
    }

    // API pour vérifier l'abonnement d'un utilisateur
    @GetMapping("/check-subscription/{userId}")
    @ResponseBody
    public ResponseEntity<?> checkSubscription(@PathVariable Integer userId) {
        try {
            boolean canEmprunter = abonnementService.canEmprunter(userId);
            String message = abonnementService.getMessageAbonnement(userId);
            return ResponseEntity.ok(new AbonnementInfo(canEmprunter, message));
        } catch (Exception e) {
            e.printStackTrace(); // ou log.error(...)
            return ResponseEntity.status(500).body(new AbonnementInfo(false, "Erreur lors de la vérification de l'abonnement"));
        }
    }
    
    @GetMapping("/check-emprunts/{userId}")
    @ResponseBody
    public ResponseEntity<?> checkEmprunts(@PathVariable Integer userId) {
        try {
            boolean canEmprunterPlus = abonnementService.canEmprunterPlus(userId);
            String message = abonnementService.getMessageEmprunts(userId);
            return ResponseEntity.ok(new EmpruntsInfo(canEmprunterPlus, message));
        } catch (Exception e) {
            e.printStackTrace(); // ou log.error(...)
            return ResponseEntity.status(500).body(new EmpruntsInfo(false, "Erreur lors de la vérification des emprunts"));
        }
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