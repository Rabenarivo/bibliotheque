package itu.biblio.repositories;

import itu.biblio.entities.Emprunt;
import itu.biblio.projection.EmpruntProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmpruntRepository extends JpaRepository<Emprunt, Integer> {
    
    @Query("SELECT e.id AS empruntId, u.nom AS utilisateurNom, u.prenom AS utilisateurPrenom, " +
           "u.email AS utilisateurEmail, l.titre AS livreTitre, l.auteur AS livreAuteur, " +
           "e.dateEmprunt AS dateEmprunt, e.dateRetour AS dateRetour, e.statutEmprunt AS statutEmprunt, " +
           "ed.dateRetour AS dateRetourEffective, " +
           "CASE WHEN e.dateRetour < CURRENT_DATE AND e.statutEmprunt IN ('en_cours', 'En cours') " +
           "THEN CAST(CURRENT_DATE - e.dateRetour AS integer) ELSE 0 END AS joursDeRetard " +
           "FROM Emprunt e " +
           "LEFT JOIN e.utilisateur u " +
           "LEFT JOIN EmpruntDetail ed ON ed.emprunt.id = e.id " +
           "LEFT JOIN ed.livre l " +
           "ORDER BY e.dateEmprunt DESC")
    List<EmpruntProjection> findAllEmpruntsWithDetails();

    @Query("SELECT e.id AS empruntId, u.nom AS utilisateurNom, u.prenom AS utilisateurPrenom, " +
           "u.email AS utilisateurEmail, l.titre AS livreTitre, l.auteur AS livreAuteur, " +
           "e.dateEmprunt AS dateEmprunt, e.dateRetour AS dateRetour, e.statutEmprunt AS statutEmprunt, " +
           "ed.dateRetour AS dateRetourEffective, " +
           "CASE WHEN e.dateRetour < CURRENT_DATE AND e.statutEmprunt IN ('en_cours', 'En cours') " +
           "THEN CAST(CURRENT_DATE - e.dateRetour AS integer) ELSE 0 END AS joursDeRetard " +
           "FROM Emprunt e " +
           "LEFT JOIN e.utilisateur u " +
           "LEFT JOIN EmpruntDetail ed ON ed.emprunt.id = e.id " +
           "LEFT JOIN ed.livre l " +
           "WHERE e.id = :id")
    Optional<EmpruntProjection> findEmpruntByIdWithDetails(@Param("id") Integer id);

    long countByStatutEmprunt(String statutEmprunt);

    @Query("SELECT COUNT(e) FROM Emprunt e WHERE e.dateRetour < CURRENT_DATE AND e.statutEmprunt IN ('en_cours', 'En cours')")
    long countEmpruntsEnRetard();

    /**
     * Trouve les emprunts par statut
     */
    List<Emprunt> findByStatutEmprunt(String statutEmprunt);

    /**
     * Trouve les emprunts par statut dans une liste
     */
    List<Emprunt> findByStatutEmpruntIn(List<String> statuts);

    /**
     * Trouve les emprunts en retard (date de retour dépassée et statut en cours)
     */
    @Query("SELECT e FROM Emprunt e WHERE e.dateRetour < CURRENT_DATE AND e.statutEmprunt IN ('en_cours', 'En cours')")
    List<Emprunt> findEmpruntsEnRetard();

    /**
     * Compte les emprunts en retard par utilisateur
     */
    @Query("SELECT COUNT(e) FROM Emprunt e WHERE e.dateRetour < CURRENT_DATE AND e.statutEmprunt IN ('en_cours', 'En cours') AND e.utilisateur.id = :userId")
    long countEmpruntsEnRetardByUtilisateur(@Param("userId") Integer userId);
} 