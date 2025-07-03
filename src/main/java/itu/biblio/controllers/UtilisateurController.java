package itu.biblio.controllers;

import itu.biblio.entities.Utilisateur;
import itu.biblio.services.UtilisateurServices;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class UtilisateurController {

    private final UtilisateurServices utilisateurServices;

    public UtilisateurController (UtilisateurServices utilisateurServices) {
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

    @GetMapping("/login")
    public String showLoginForm() {
        return "login"; 
    }

    @PostMapping("/login")
    public String loginUtilisateur(@RequestParam String email, @RequestParam String mdp, HttpSession session) {
        try {
            Utilisateur loggedInUser = utilisateurServices.login(email, mdp);
            session.setAttribute("userId", loggedInUser.getId());
            return "redirect:/profile";
        } catch (RuntimeException e) {
            return "redirect:/login?error";
        }
    }

    @GetMapping("/profile")
    public String showProfile(Model model, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/utilisateur/login";
        }
        
        Optional<Utilisateur> utilisateur = utilisateurServices.getUtilisateurById(userId);
        if (utilisateur.isPresent()) {
            model.addAttribute("utilisateur", utilisateur.get());
            return "profile";
        }
        return "redirect:/utilisateur/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/utilisateur/login";
    }
}
