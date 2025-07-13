package itu.biblio.services;

import itu.biblio.entities.DemandeProlongement;
import itu.biblio.entities.Emprunt;
import itu.biblio.entities.Utilisateur;
import itu.biblio.repositories.DemandeProlongementRepository;
import itu.biblio.repositories.EmpruntRepository;
import itu.biblio.repositories.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class DemandeProlongementService {
    
    @Autowired
    private DemandeProlongementRepository demandeProlongementRepository;
    
    @Autowired
    private EmpruntRepository empruntRepository;
    
    @Autowired
    private UtilisateurRepository utilisateurRepository;
    
    @Autowired
    private AbonnementService abonnementService;
    
    // Créer une nouvelle demande de prolongement
    @Transactional
    public DemandeProlongement creerDemande(Integer empruntId, Integer utilisateurId, 
                                          LocalDate dateProlongementDemandee, String motif, 
                                          LocalDate dateDemande) {
        
        // Vérifier que l'emprunt existe et appartient à l'utilisateur
        Optional<Emprunt> empruntOpt = empruntRepository.findById(empruntId);
        if (!empruntOpt.isPresent()) {
            throw new IllegalArgumentException("Emprunt non trouvé");
        }
        
        Emprunt emprunt = empruntOpt.get();
        if (!emprunt.getUtilisateur().getId().equals(utilisateurId)) {
            throw new IllegalArgumentException("Cet emprunt ne vous appartient pas");
        }
        
        // Vérifier que l'emprunt est en cours
        if (!"en_cours".equals(emprunt.getStatutEmprunt()) && !"En cours".equals(emprunt.getStatutEmprunt())) {
            throw new IllegalArgumentException("L'emprunt n'est pas en cours");
        }
        
        // Vérifier qu'il n'y a pas déjà une demande en attente pour cet emprunt
        Optional<DemandeProlongement> demandeExistante = demandeProlongementRepository
            .findByEmpruntIdAndUtilisateurId(empruntId, utilisateurId);
        
        if (demandeExistante.isPresent() && "en_attente".equals(demandeExistante.get().getStatut())) {
            throw new IllegalArgumentException("Une demande de prolongement est déjà en attente pour cet emprunt");
        }
        
        // Vérifier le nombre de demandes en attente de l'utilisateur
        long demandesEnAttente = demandeProlongementRepository.countByUtilisateurIdAndStatut(utilisateurId, "en_attente");
        if (demandesEnAttente >= 3) {
            throw new IllegalArgumentException("Vous avez déjà 3 demandes de prolongement en attente");
        }
        
        // Vérifier que la nouvelle date est postérieure à la date de demande
        if (dateProlongementDemandee.isBefore(dateDemande)) {
            throw new IllegalArgumentException("La date de prolongement doit être postérieure à la date de demande");
        }
        
        // Créer la demande
        DemandeProlongement demande = new DemandeProlongement();
        demande.setEmprunt(emprunt);
        demande.setUtilisateur(emprunt.getUtilisateur());
        demande.setDateDemande(dateDemande);
        demande.setDateProlongementDemandee(dateProlongementDemandee);
        demande.setStatut("en_attente");
        demande.setMotif(motif);
        
        return demandeProlongementRepository.save(demande);
    }
    
    // Approuver une demande de prolongement
    @Transactional
    public void approuverDemande(Integer demandeId, Integer adminId) {
        Optional<DemandeProlongement> demandeOpt = demandeProlongementRepository.findById(demandeId);
        if (!demandeOpt.isPresent()) {
            throw new IllegalArgumentException("Demande non trouvée");
        }
        
        DemandeProlongement demande = demandeOpt.get();
        if (!"en_attente".equals(demande.getStatut())) {
            throw new IllegalArgumentException("Cette demande ne peut plus être approuvée");
        }
        
        // Vérifier que l'admin existe
        Optional<Utilisateur> adminOpt = utilisateurRepository.findById(adminId);
        if (!adminOpt.isPresent() || !adminOpt.get().isAdmin()) {
            throw new IllegalArgumentException("Admin non trouvé ou non autorisé");
        }
        
        // Mettre à jour l'emprunt
        Emprunt emprunt = demande.getEmprunt();
        emprunt.setDateRetour(demande.getDateProlongementDemandee());
        empruntRepository.save(emprunt);
        
        // Mettre à jour la demande
        demande.setStatut("acceptee");
        demande.setDateValidation(LocalDate.now());
        demande.setAdminValidateur(adminOpt.get());
        demandeProlongementRepository.save(demande);
        
        // Diminuer le quota de l'utilisateur (compte comme 1 quota supplémentaire)
        abonnementService.diminuerQuota(demande.getUtilisateur().getId());
    }
    
    // Refuser une demande de prolongement
    @Transactional
    public void refuserDemande(Integer demandeId, Integer adminId, String motifRefus) {
        Optional<DemandeProlongement> demandeOpt = demandeProlongementRepository.findById(demandeId);
        if (!demandeOpt.isPresent()) {
            throw new IllegalArgumentException("Demande non trouvée");
        }
        
        DemandeProlongement demande = demandeOpt.get();
        if (!"en_attente".equals(demande.getStatut())) {
            throw new IllegalArgumentException("Cette demande ne peut plus être refusée");
        }
        
        // Vérifier que l'admin existe
        Optional<Utilisateur> adminOpt = utilisateurRepository.findById(adminId);
        if (!adminOpt.isPresent() || !adminOpt.get().isAdmin()) {
            throw new IllegalArgumentException("Admin non trouvé ou non autorisé");
        }
        
        // Mettre à jour la demande
        demande.setStatut("refusee");
        demande.setDateValidation(LocalDate.now());
        demande.setAdminValidateur(adminOpt.get());
        demande.setMotif(demande.getMotif() + " [REFUSÉ: " + motifRefus + "]");
        demandeProlongementRepository.save(demande);
    }
    
    // Obtenir toutes les demandes d'un utilisateur
    public List<DemandeProlongement> getDemandesByUtilisateur(Integer utilisateurId) {
        return demandeProlongementRepository.findByUtilisateurId(utilisateurId);
    }
    
    // Obtenir toutes les demandes en attente
    public List<DemandeProlongement> getDemandesEnAttente() {
        return demandeProlongementRepository.findByStatutOrderByDateDemandeDesc("en_attente");
    }
    
    // Obtenir toutes les demandes avec détails
    public List<DemandeProlongement> getAllDemandesWithDetails() {
        return demandeProlongementRepository.findAllWithDetails();
    }
    
    // Vérifier si un utilisateur peut demander une prolongement
    public boolean peutDemanderProlongement(Integer utilisateurId) {
        long demandesEnAttente = demandeProlongementRepository.countByUtilisateurIdAndStatut(utilisateurId, "en_attente");
        return demandesEnAttente < 3;
    }
    
    // Vérifier si un emprunt peut être prolongé
    public boolean peutProlongerEmprunt(Integer empruntId, Integer utilisateurId) {
        Optional<Emprunt> empruntOpt = empruntRepository.findById(empruntId);
        if (!empruntOpt.isPresent()) {
            return false;
        }
        
        Emprunt emprunt = empruntOpt.get();
        
        // Vérifier que l'emprunt appartient à l'utilisateur
        if (!emprunt.getUtilisateur().getId().equals(utilisateurId)) {
            return false;
        }
        
        // Vérifier que l'emprunt est en cours
        if (!"en_cours".equals(emprunt.getStatutEmprunt()) && !"En cours".equals(emprunt.getStatutEmprunt())) {
            return false;
        }
        
        // Vérifier qu'il n'y a pas déjà une demande en attente
        Optional<DemandeProlongement> demandeExistante = demandeProlongementRepository
            .findByEmpruntIdAndUtilisateurId(empruntId, utilisateurId);
        
        if (demandeExistante.isPresent() && "en_attente".equals(demandeExistante.get().getStatut())) {
            return false;
        }
        
        return true;
    }
    
    // Obtenir une demande par ID
    public Optional<DemandeProlongement> getDemandeById(Integer demandeId) {
        return demandeProlongementRepository.findById(demandeId);
    }
} 