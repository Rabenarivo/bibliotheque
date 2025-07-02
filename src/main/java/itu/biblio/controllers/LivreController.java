package itu.biblio.controllers;

import itu.biblio.entities.Livre;
import itu.biblio.projection.ListeLivreParAdherantProjection;
import itu.biblio.services.LivreServices;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class LivreController {
    private final LivreServices livreService;

    public LivreController(LivreServices livreService) {
        this.livreService = livreService;
    }

    @GetMapping("/livres")
    public List<ListeLivreParAdherantProjection> getAllLivres(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        List<ListeLivreParAdherantProjection> livres = livreService.getAllLivres(userId);
        model.addAttribute("livres", livres);
        return livres;
    }

}
