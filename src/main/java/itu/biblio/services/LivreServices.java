package itu.biblio.services;

import itu.biblio.entities.Livre;
import itu.biblio.projection.ListeLivreParAdherantProjection;
import itu.biblio.projection.LivreDisponibiliteProjection;
import itu.biblio.repositories.LivreRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LivreServices {

    private final LivreRepository livreRepository;

    public LivreServices(LivreRepository livreRepository) {
        this.livreRepository = livreRepository;
    }

    public Iterable<ListeLivreParAdherantProjection> getListeLivrePourAdherant(Integer userId) {
        return livreRepository.findLivresByUserId(userId);
    }

    public LivreDisponibiliteProjection getDisponibiliteLivre(Integer livreId) {
        return livreRepository.getDisponibiliteLivre(livreId);
    }

}
