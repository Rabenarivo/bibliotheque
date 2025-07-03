package itu.biblio.repositories;

import itu.biblio.entities.HistoriqueLivre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoriqueLivreRepository extends JpaRepository<HistoriqueLivre, Integer> {
    
}