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
    //isprsent renvoie une valeur nil ou non dans la class optimal<>
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
    
//quota
    public boolean canEmprunterPlus(Integer userId) {
        try {
            // Récupérer l'utilisateur et son adhérent
            Utilisateur utilisateur = utilisateurServices.getUtilisateurById(userId).orElse(null);
            if (utilisateur == null || utilisateur.getIdAdherant() == null) {
                return false;
            }
            
            // Récupérer la limite d'emprunts de l'adhérent
            Integer limiteEmprunts = utilisateur.getIdAdherant().getNbrLivrePret();
            if (limiteEmprunts == null) {
                return false;
            }
            
            // Compter les emprunts en cours de l'utilisateur
            long empruntsEnCours = countEmpruntsEnCours(userId);
            
            return empruntsEnCours < limiteEmprunts;
        } catch (Exception e) {
            return false;
        }
    }
    
//nbr_quota
    public long countEmpruntsEnCours(Integer userId) {
        return abonnementRepository.countEmpruntsEnCoursByUtilisateur(userId);
    }

    public String getMessageEmprunts(Integer userId) {
        try {
            Utilisateur utilisateur = utilisateurServices.getUtilisateurById(userId).orElse(null);
            if (utilisateur == null || utilisateur.getIdAdherant() == null) {
                return " Utilisateur ou adhérent non trouvé";
            }
            
            Integer limiteEmprunts = utilisateur.getIdAdherant().getNbrLivrePret();
            if (limiteEmprunts == null) {
                return " Limite d'emprunts non définie";
            }
            
            long empruntsEnCours = countEmpruntsEnCours(userId);
            long empruntsRestants = limiteEmprunts - empruntsEnCours;
            
            if (empruntsEnCours >= limiteEmprunts) {
                return " Limite d'emprunts atteinte (" + empruntsEnCours + "/" + limiteEmprunts + ")";
            } else if (empruntsRestants <= 2) {
                return " Plus que " + empruntsRestants + " emprunt(s) possible(s) (" + empruntsEnCours + "/" + limiteEmprunts + ")";
            } else {
                return " " + empruntsRestants + " emprunt(s) possible(s) (" + empruntsEnCours + "/" + limiteEmprunts + ")";
            }
        } catch (Exception e) {
            return " Erreur lors de la vérification des emprunts";
        }
    }
} 