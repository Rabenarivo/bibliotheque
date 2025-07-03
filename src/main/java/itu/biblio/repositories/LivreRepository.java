package itu.biblio.repositories;

import itu.biblio.entities.Livre;
import itu.biblio.projection.ListeLivreParAdherantProjection;
import itu.biblio.services.LivreDisponibiliteProjection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LivreRepository extends JpaRepository<Livre, Integer> {

    @Query(value = """
            SELECT l.id AS id, l.titre AS titre, l.auteur AS auteur, l.age AS age, l.image AS image
            FROM livre l
            JOIN livre_adherant la ON l.id = la.livre_id
            JOIN adherant a ON la.adherant_id = a.id
            JOIN utilisateur u ON u.id_adherant = a.id
            WHERE u.id = :id
            """, nativeQuery = true)
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
    List <LivreDisponibiliteProjection> findLivreDisponibiliteById(@Param("idlivre") Integer idlivre);
}


