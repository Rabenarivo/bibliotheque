package itu.biblio.repositories;

import itu.biblio.entities.Livre;
import itu.biblio.projection.ListeLivreParAdherantProjection;
import itu.biblio.projection.LivreDisponibiliteProjection;

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



        @Query(value = """
        SELECT 
            l.id AS livreId,
            l.titre AS titre,
            l.examplaire AS totalExemplaires,
            COUNT(CASE WHEN sl.nom = 'en_cours_de_pret' THEN hl.id END) AS exemplairesIndisponibles,
            (l.examplaire - COUNT(CASE WHEN sl.nom = 'en_cours_de_pret' THEN hl.id END)) AS exemplairesDisponibles
        FROM 
            livre l
        LEFT JOIN 
            historique_livre hl ON l.id = hl.livre_id
        LEFT JOIN 
            statut_livre sl ON hl.statut_id = sl.id
        WHERE 
            l.id = :idlivre
        GROUP BY 
            l.id, l.titre, l.examplaire
    """, nativeQuery = true)
    LivreDisponibiliteProjection findLivreDisponibiliteById(@Param("idlivre") Integer idlivre);

}