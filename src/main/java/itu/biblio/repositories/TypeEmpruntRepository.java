package itu.biblio.repositories;

import itu.biblio.entities.TypeEmprunt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TypeEmpruntRepository extends JpaRepository<TypeEmprunt, Integer> {
} 