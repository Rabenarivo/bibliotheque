package itu.biblio.repositories;

import itu.biblio.entities.DemandeProlongement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DemandeProlongementRepository extends JpaRepository<DemandeProlongement, Integer> {
    
    // Trouver toutes les demandes d'un utilisateur
    List<DemandeProlongement> findByUtilisateurId(Integer utilisateurId);
    
    // Trouver toutes les demandes en attente
    List<DemandeProlongement> findByStatut(String statut);
    
    // Trouver les demandes en attente
    List<DemandeProlongement> findByStatutOrderByDateDemandeDesc(String statut);
    
    // Trouver les demandes d'un emprunt spécifique
    List<DemandeProlongement> findByEmpruntId(Integer empruntId);
    
    // Compter les demandes en attente d'un utilisateur
    long countByUtilisateurIdAndStatut(Integer utilisateurId, String statut);
    
    // Trouver une demande par emprunt et utilisateur
    Optional<DemandeProlongement> findByEmpruntIdAndUtilisateurId(Integer empruntId, Integer utilisateurId);
    
    // Requête personnalisée pour obtenir toutes les demandes avec détails
    @Query("SELECT dp FROM DemandeProlongement dp " +
           "LEFT JOIN FETCH dp.emprunt e " +
           "LEFT JOIN FETCH dp.utilisateur u " +
           "LEFT JOIN FETCH dp.adminValidateur a " +
           "ORDER BY dp.dateDemande DESC")
    List<DemandeProlongement> findAllWithDetails();
} 