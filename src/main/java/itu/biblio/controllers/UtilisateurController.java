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
    public String showLoginForm(Model model) {
        model.addAttribute("utilisateur", new Utilisateur());
        return "login"; 
    }

    @PostMapping("/login")
    public String loginUtilisateur(@ModelAttribute("utilisateur") Utilisateur utilisateur, HttpSession session) {
        try {
            Utilisateur loggedInUser = utilisateurServices.login(utilisateur.getEmail(), utilisateur.getMdp());
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
            return "redirect:/login";
        }
        
        Optional<Utilisateur> utilisateur = utilisateurServices.getUtilisateurById(userId);
        if (utilisateur.isPresent()) {
            Utilisateur user = utilisateur.get();
            model.addAttribute("utilisateur", user);
            
            if (user.isAdmin()) {
                return "redirect:/admin/reservations";
            }
            return "profile";
        }
        return "redirect:/login";
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/utilisateur/login";
    }
}
