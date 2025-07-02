package itu.biblio.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import itu.biblio.repositories.AdherantRepository;

import itu.biblio.entities.*;
import itu.biblio.controllers.UtilisateurController;
import itu.biblio.projection.ListeLivreParAdherantProjection;
import itu.biblio.controllers.LivreController;
import itu.biblio.repositories.LivreRepository;
import itu.biblio.repositories.UtilisateurRepository;

import java.util.List;
import java.util.Optional;

@Service
public class LivreServices {
    private final LivreRepository livreRepository;

    public LivreServices(LivreRepository livreRepository) {
        this.livreRepository = livreRepository;
    }

    public List<ListeLivreParAdherantProjection> getAllLivres(Integer id) {
        return livreRepository.findAvailableLivres(id);
    }
    
}
