package itu.biblio.repositories;

import itu.biblio.entities.Penalite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PenaliteRepository extends JpaRepository<Penalite, Integer> {
    List<Penalite> findByUtilisateurId(Integer utilisateurId);
    List<Penalite> findByEmpruntId(Integer empruntId);
    List<Penalite> findByUtilisateurIdAndEmpruntId(Integer utilisateurId, Integer empruntId);
}
