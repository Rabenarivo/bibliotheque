package itu.biblio.repositories;

import itu.biblio.entities.EmpruntDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmpruntDetailsRepository extends JpaRepository<EmpruntDetail, Integer> {
    
    @Query("SELECT ed FROM EmpruntDetail ed WHERE ed.emprunt.id = :empruntId")
    List<EmpruntDetail> findByEmpruntId(@Param("empruntId") Integer empruntId);
} 