package itu.biblio.repositories;



import itu.biblio.entities.StatutLivre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatutLivreRepository extends JpaRepository<StatutLivre, Integer> {
    
} 
