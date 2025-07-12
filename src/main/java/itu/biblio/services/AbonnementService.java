package itu.biblio.services;

import itu.biblio.entities.Abonnement;
import itu.biblio.entities.Utilisateur;
import itu.biblio.repositories.AbonnementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AbonnementService {
    
    @Autowired
    private AbonnementRepository abonnementRepository;
    
    @Autowired
    private UtilisateurServices utilisateurServices;

    public List<Abonnement> getAllAbonnements() {
        return abonnementRepository.findAll();
    }

    public Optional<Abonnement> getAbonnementById(Integer id) {
        return abonnementRepository.findById(id);
    }

    public Abonnement saveAbonnement(Abonnement abonnement) {
        return abonnementRepository.save(abonnement);
    }

    public void deleteAbonnement(Integer id) {
        abonnementRepository.deleteById(id);
    }

    public List<Abonnement> getAbonnementsByUtilisateur(Integer userId) {
        return abonnementRepository.findByUtilisateurId(userId);
    }

    public Optional<Abonnement> getAbonnementActifByUtilisateur(Integer userId) {
        return abonnementRepository.findAbonnementActifByUtilisateur(userId, LocalDate.now());
    }

    public boolean isUtilisateurAbonne(Integer userId) {
        Optional<Abonnement> abonnementActif = getAbonnementActifByUtilisateur(userId);
        return abonnementActif.isPresent();
    }
    
    public boolean canEmprunter(Integer userId) {
        // Vérifier si l'utilisateur a un abonnement actif
        Optional<Abonnement> abonnementActif = getAbonnementActifByUtilisateur(userId);
        if (!abonnementActif.isPresent()) {
            return false;
        }

        // Vérifier si l'abonnement n'est pas expiré
        Abonnement abonnement = abonnementActif.get();
        LocalDate aujourdhui = LocalDate.now();
        
        return !aujourdhui.isAfter(abonnement.getDateFin());
    }

    public boolean canEmprunterUntil(Integer userId, LocalDate dateRetour) {
        // Vérifier si l'utilisateur a un abonnement actif
        Optional<Abonnement> abonnementActif = getAbonnementActifByUtilisateur(userId);
        if (!abonnementActif.isPresent()) {
            return false;
        }

        // Vérifier si l'abonnement ne expire pas avant la date de retour
        Abonnement abonnement = abonnementActif.get();
        LocalDate aujourdhui = LocalDate.now();
        
        // L'abonnement doit être actif aujourd'hui ET ne pas expirer avant la date de retour
        return !aujourdhui.isAfter(abonnement.getDateFin()) && 
               !dateRetour.isAfter(abonnement.getDateFin());
    }

    public String getMessageAbonnement(Integer userId) {
        Optional<Abonnement> abonnementActif = getAbonnementActifByUtilisateur(userId);
        
        if (!abonnementActif.isPresent()) {
            return " Aucun abonnement actif trouvé";
        }

        Abonnement abonnement = abonnementActif.get();
        LocalDate aujourdhui = LocalDate.now();
        
        if (aujourdhui.isAfter(abonnement.getDateFin())) {
            return " Abonnement expiré le " + abonnement.getDateFin().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }
        
        long joursRestants = java.time.temporal.ChronoUnit.DAYS.between(aujourdhui, abonnement.getDateFin());
        
        if (joursRestants <= 7) {
            return " Abonnement expire dans " + joursRestants + " jour(s) - " + abonnement.getDateFin().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } else {
            return " Abonnement valide jusqu'au " + abonnement.getDateFin().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " (" + joursRestants + " jours restants)";
        }
    }
    
    public boolean canEmprunterPlus(Integer userId) {
        try {
            Utilisateur utilisateur = utilisateurServices.getUtilisateurById(userId).orElse(null);
            if (utilisateur == null) {
                return false;
            }
            
            return utilisateur.peutEmprunter();
        } catch (Exception e) {
            return false;
        }
    }
    
    public void diminuerQuota(Integer userId) {
        try {
            Utilisateur utilisateur = utilisateurServices.getUtilisateurById(userId).orElse(null);
            if (utilisateur != null) {
                utilisateur.diminuerQuota();
                utilisateurServices.saveUtilisateur(utilisateur);
            }
        } catch (Exception e) {
        }
    }
    
    public void augmenterQuota(Integer userId) {
        try {
            Utilisateur utilisateur = utilisateurServices.getUtilisateurById(userId).orElse(null);
            if (utilisateur != null) {
                utilisateur.augmenterQuota();
                utilisateurServices.saveUtilisateur(utilisateur);
            }
        } catch (Exception e) {
        }
    }
    
    public void initialiserQuota(Integer userId) {
        try {
            Utilisateur utilisateur = utilisateurServices.getUtilisateurById(userId).orElse(null);
            if (utilisateur != null) {
                utilisateur.initialiserQuota();
                utilisateurServices.saveUtilisateur(utilisateur);
            }
        } catch (Exception e) {
        }
    }

    public String getMessageEmprunts(Integer userId) {
        try {
            Utilisateur utilisateur = utilisateurServices.getUtilisateurById(userId).orElse(null);
            if (utilisateur == null || utilisateur.getIdAdherant() == null) {
                return " Utilisateur ou adhérent non trouvé";
            }
            
            Integer limiteEmprunts = utilisateur.getIdAdherant().getNbrLivrePret();
            Integer quotaActuel = utilisateur.getQuotaActuel();
            
            if (limiteEmprunts == null) {
                return " Limite d'emprunts non définie";
            }
            
            if (quotaActuel == null) {
                initialiserQuota(userId);
                quotaActuel = utilisateur.getQuotaActuel();
            }
            
            if (quotaActuel <= 0) {
                return " Quota d'emprunts épuisé (0/" + limiteEmprunts + ")";
            } else if (quotaActuel <= 2) {
                return " Plus que " + quotaActuel + " emprunt(s) possible(s) (" + quotaActuel + "/" + limiteEmprunts + ")";
            } else {
                return " " + quotaActuel + " emprunt(s) possible(s) (" + quotaActuel + "/" + limiteEmprunts + ")";
            }
        } catch (Exception e) {
            return " Erreur lors de la vérification des emprunts";
        }
    }
} 