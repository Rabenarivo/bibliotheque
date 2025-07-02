package itu.biblio.controllers;

import itu.biblio.entities.Utilisateur;
import itu.biblio.services.UtilisateurServices;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/utilisateur")
public class UtilisateurController {

    private final UtilisateurServices utilisateurServices;

    public UtilisateurController(UtilisateurServices utilisateurServices) {
        this.utilisateurServices = utilisateurServices;
    }

    @GetMapping("/inscription")
    public String showInscriptionForm(Model model) {
        model.addAttribute("utilisateur", new Utilisateur());
        model.addAttribute("adherants", utilisateurServices.getAllAdherants());
        return "inscription"; 
    }

    @PostMapping("/inscription")
    public String registerUtilisateur(@ModelAttribute("utilisateur") Utilisateur utilisateur) {
        utilisateurServices.registerUtilisateur(utilisateur); 
        return "redirect:/utilisateur/inscription?success";
    }
}
