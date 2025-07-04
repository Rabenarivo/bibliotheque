package itu.biblio.services;

import itu.biblio.entities.Emprunt;
import itu.biblio.entities.Penalite;
import itu.biblio.entities.Utilisateur;
import itu.biblio.repositories.PenaliteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PenaliteService {

    private final PenaliteRepository penaliteRepository;

    @Autowired
    public PenaliteService(PenaliteRepository penaliteRepository) {
        this.penaliteRepository = penaliteRepository;
    }

    public List<Penalite> getAllPenalites() {
        return penaliteRepository.findAll();
    }

    public Optional<Penalite> getPenaliteById(Integer id) {
        return penaliteRepository.findById(id);
    }

    public List<Penalite> getPenalitesByUtilisateur(Integer utilisateurId) {
        return penaliteRepository.findByUtilisateurId(utilisateurId);
    }

    public List<Penalite> getPenalitesByEmprunt(Integer empruntId) {
        return penaliteRepository.findByEmpruntId(empruntId);
    }

    public List<Penalite> getPenalitesByUtilisateurAndEmprunt(Integer utilisateurId, Integer empruntId) {
        return penaliteRepository.findByUtilisateurIdAndEmpruntId(utilisateurId, empruntId);
    }

    public Penalite savePenalite(Penalite penalite) {
        return penaliteRepository.save(penalite);
    }

    public void deletePenalite(Integer id) {
        penaliteRepository.deleteById(id);
    }

    public boolean isUserUnderPenalty(Integer utilisateurId) {
        List<Penalite> penalites = penaliteRepository.findByUtilisateurId(utilisateurId);
        LocalDate now = LocalDate.now();
        return penalites.stream().anyMatch(p -> now.isBefore(p.getDateDebut().plusDays(p.getNbJourSanction())));
    }

    public void applyPenalty(Integer utilisateurId, Integer empruntId, int days) {
        if (isUserUnderPenalty(utilisateurId)) {
            // L'utilisateur a déjà une pénalité active, on ne fait rien ou on lève une exception
            return;
        }
        Penalite penalite = new Penalite();
        
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(utilisateurId);
        penalite.setUtilisateur(utilisateur);
        
        Emprunt emprunt = new Emprunt();
        emprunt.setId(empruntId);
        penalite.setEmprunt(emprunt);
        
        penalite.setSanction(1);
        penalite.setNbJourSanction(days);
        penalite.setDateDebut(LocalDate.now());
        
        penaliteRepository.save(penalite);
    }
}