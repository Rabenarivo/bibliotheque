package itu.biblio.repositories;

import itu.biblio.entities.Livre;
import itu.biblio.projection.ListeLivreParAdherantProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LivreRepository extends JpaRepository<Livre, Integer> {
    
    @Query("SELECT l.id AS id, l.titre AS titre, l.auteur AS auteur, l.age AS age, l.image AS image " +
           "FROM Livre l " +
           "JOIN LivreAdherant la ON l.id = la.livre.id " +
           "JOIN Adherant a ON la.adherant.id = a.id " +
           "JOIN Utilisateur u ON u.idAdherant.id = a.id " +
           "WHERE u.id = :id")
    List<ListeLivreParAdherantProjection> findAvailableLivres(@Param("id") Integer id);
}

