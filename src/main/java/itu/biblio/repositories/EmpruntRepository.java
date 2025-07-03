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
           "ed.dateRetour AS dateRetourEffective " +
           "FROM Emprunt e " +
           "LEFT JOIN e.utilisateur u " +
           "LEFT JOIN EmpruntDetail ed ON ed.emprunt = e " +
           "LEFT JOIN ed.livre l " +
           "ORDER BY e.dateEmprunt DESC")
    List<EmpruntProjection> findAllEmpruntsWithDetails();

    @Query("SELECT e.id AS empruntId, u.nom AS utilisateurNom, u.prenom AS utilisateurPrenom, " +
           "u.email AS utilisateurEmail, l.titre AS livreTitre, l.auteur AS livreAuteur, " +
           "e.dateEmprunt AS dateEmprunt, e.dateRetour AS dateRetour, e.statutEmprunt AS statutEmprunt, " +
           "ed.dateRetour AS dateRetourEffective " +
           "FROM Emprunt e " +
           "LEFT JOIN e.utilisateur u " +
           "LEFT JOIN EmpruntDetail ed ON ed.emprunt = e " +
           "LEFT JOIN ed.livre l " +
           "WHERE e.id = :empruntId")
    Optional<EmpruntProjection> findEmpruntByIdWithDetails(@Param("empruntId") Integer empruntId);

    long countByStatutEmprunt(String statutEmprunt);

    @Query("SELECT COUNT(e) FROM Emprunt e WHERE e.statutEmprunt = 'en_cours' AND e.dateRetour < CURRENT_DATE")
    long countEmpruntsEnRetard();
} 