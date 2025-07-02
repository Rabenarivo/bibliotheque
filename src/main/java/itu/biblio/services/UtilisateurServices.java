package itu.biblio.services;

import org.springframework.stereotype.Service;

import itu.biblio.repositories.AdherantRepository;

import itu.biblio.entities.*;
import itu.biblio.controllers.UtilisateurController;
import itu.biblio.repositories.UtilisateurRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UtilisateurServices {

    private final UtilisateurRepository utilisateurRepository;
    private final AdherantRepository adherantRepository;

    public UtilisateurServices(UtilisateurRepository utilisateurRepository, AdherantRepository adherantRepository) {
        this.utilisateurRepository = utilisateurRepository;
        this.adherantRepository = adherantRepository;
    }

    public List<Utilisateur> getAllUtilisateurs() {
        return utilisateurRepository.findAll();
    }

    public Optional<Utilisateur> getUtilisateurById(Integer id) {
        return utilisateurRepository.findById(id);
    }

    public List<Adherant> getAllAdherants() {
        return adherantRepository.findAll();
    }

    public void registerUtilisateur(Utilisateur utilisateur) {
        if (utilisateur.getIdAdherant() != null) {
            Adherant adherant = adherantRepository.findById(utilisateur.getIdAdherant().getId())
                .orElseThrow(() -> new RuntimeException("Adherant not found"));
            utilisateur.setIdAdherant(adherant);
        }
        utilisateurRepository.save(utilisateur);
    }

    public Utilisateur login(String email, String password) {
        Optional<Utilisateur> utilisateur = utilisateurRepository.findByEmail(email);
        if (utilisateur.isPresent() && utilisateur.get().getMdp().equals(password)) {
            return utilisateur.get();
        }
        throw new RuntimeException("Invalid email or password");
    }

}
