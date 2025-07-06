package itu.biblio.services;

import org.springframework.stereotype.Service;

import itu.biblio.repositories.HistoriqueLivreRepository;
import itu.biblio.repositories.StatutLivreRepository;

import itu.biblio.entities.*;
import itu.biblio.projection.ListeLivreParAdherantProjection;
import itu.biblio.projection.LivreDisponibiliteProjection;
import itu.biblio.repositories.LivreRepository;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class LivreServices {
    private final LivreRepository livreRepository;
    private final HistoriqueLivreRepository historiqueLivreRepository;
    private final StatutLivreRepository statutLivreRepository;

    public LivreServices(LivreRepository livreRepository, 
                        HistoriqueLivreRepository historiqueLivreRepository,
                        StatutLivreRepository statutLivreRepository) {
        this.livreRepository = livreRepository;
        this.historiqueLivreRepository = historiqueLivreRepository;
        this.statutLivreRepository = statutLivreRepository;
    }

    public List<ListeLivreParAdherantProjection> getAllLivres(Integer id) {
        try {
            return livreRepository.findAvailableLivres(id);
        } catch (Exception e) {
            // Log l'erreur si besoin
            return java.util.Collections.emptyList();
        }
    }

    public List<Livre> getAllLivresForAdmin() {
        return livreRepository.findAll();
    }

    public Optional<Livre> getLivreById(Integer id) {
        return livreRepository.findById(id);
    }

    public LivreDisponibiliteProjection getLivreDisponibiliteById(Integer livreId) {
        return livreRepository.findLivreDisponibiliteById(livreId);
    }

    public LivreDisponibiliteProjection getLivreDisponibiliteReelleById(Integer livreId) {
        return livreRepository.findLivreDisponibiliteReelleById(livreId);
    }

    public void updateLivreStatus(Integer livreId, String statutNom) {
        Livre livre = livreRepository.findById(livreId)
            .orElseThrow(() -> new RuntimeException("Livre non trouvé"));
        StatutLivre statut = statutLivreRepository.findByNom(statutNom)
            .orElseThrow(() -> new RuntimeException("Statut non trouvé: " + statutNom));
        livre.setStatutLivre(statut);
        livreRepository.save(livre);
        HistoriqueLivre historique = new HistoriqueLivre();
        historique.setLivre(livre);
        historique.setDateAction(LocalDate.now());
        historique.setTypeAction("CHANGEMENT_STATUT");
        historique.setDescription("Changement de statut vers: " + statutNom);
        historiqueLivreRepository.save(historique);
    }

    public void updateLivreStatusById(Integer livreId, Integer statutId) {
        Livre livre = livreRepository.findById(livreId)
            .orElseThrow(() -> new RuntimeException("Livre non trouvé"));
        StatutLivre statut = statutLivreRepository.findById(statutId)
            .orElseThrow(() -> new RuntimeException("Statut non trouvé: id=" + statutId));
        livre.setStatutLivre(statut);
        livreRepository.save(livre);
        HistoriqueLivre historique = new HistoriqueLivre();
        historique.setLivre(livre);
        historique.setDateAction(LocalDate.now());
        historique.setTypeAction("CHANGEMENT_STATUT");
        historique.setDescription("Changement de statut vers id: " + statutId);
        historiqueLivreRepository.save(historique);
    }
}
