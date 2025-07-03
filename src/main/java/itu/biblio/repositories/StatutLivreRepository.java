package itu.biblio.repositories;

import itu.biblio.entities.StatutLivre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StatutLivreRepository extends JpaRepository<StatutLivre, Integer> {
    
    @Query("SELECT sl FROM StatutLivre sl WHERE sl.nom = :nom")
    Optional<StatutLivre> findByNom(@Param("nom") String nom);
} 
