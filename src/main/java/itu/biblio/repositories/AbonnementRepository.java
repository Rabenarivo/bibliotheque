package itu.biblio.repositories;

import itu.biblio.entities.Abonnement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AbonnementRepository extends JpaRepository<Abonnement, Integer> {
    
    List<Abonnement> findByUtilisateurId(Integer utilisateurId);
    
    @Query("SELECT a FROM Abonnement a WHERE a.utilisateur.id = :userId AND :date BETWEEN a.dateDebut AND a.dateFin")
    Optional<Abonnement> findAbonnementActifByUtilisateur(@Param("userId") Integer userId, @Param("date") LocalDate date);
    
    @Query("SELECT a FROM Abonnement a WHERE a.utilisateur.id = :userId AND a.dateFin >= :date ORDER BY a.dateFin DESC")
    List<Abonnement> findAbonnementsValidesByUtilisateur(@Param("userId") Integer userId, @Param("date") LocalDate date);
    
    @Query("SELECT COUNT(a) FROM Abonnement a WHERE a.utilisateur.id = :userId AND :date BETWEEN a.dateDebut AND a.dateFin")
    long countAbonnementsActifsByUtilisateur(@Param("userId") Integer userId, @Param("date") LocalDate date);
    
    @Query("SELECT COUNT(e) FROM Emprunt e WHERE e.utilisateur.id = :userId AND e.statutEmprunt IN ('en_cours', 'En cours')")
    long countEmpruntsEnCoursByUtilisateur(@Param("userId") Integer userId);
} 