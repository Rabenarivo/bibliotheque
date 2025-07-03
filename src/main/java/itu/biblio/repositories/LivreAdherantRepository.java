package itu.biblio.repositories;

import itu.biblio.entities.LivreAdherant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LivreAdherantRepository extends JpaRepository<LivreAdherant, Integer> {

}