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
    
    @Query(value = """
        SELECT 
            l.id AS id,
            l.titre AS titre,
            l.auteur AS auteur,
            l.age AS age,
            l.image AS image,
            (l.examplaire - (COALESCE(historique_count, 0) + COALESCE(reservation_count, 0) + COALESCE(emprunt_count, 0))) AS exemplairesDisponibles
        FROM 
            livre l
        JOIN 
            livre_adherant la ON l.id = la.livre_id
        JOIN 
            adherant a ON la.adherant_id = a.id
        JOIN 
            utilisateur u ON u.id_adherant = a.id
        LEFT JOIN (
            SELECT 
                hl.livre_id,
                COUNT(hl.id) as historique_count
            FROM historique_livre hl
            JOIN statut_livre sl ON hl.statut_id = sl.id
            WHERE sl.nom = 'en_cours_de_pret'
            GROUP BY hl.livre_id
        ) h ON l.id = h.livre_id
        LEFT JOIN (
            SELECT 
                r.livre_id,
                COUNT(r.id) as reservation_count
            FROM reservation r
            WHERE r.est_validee = false
            GROUP BY r.livre_id
        ) res ON l.id = res.livre_id
        LEFT JOIN (
            SELECT 
                ed.livre_id,
                COUNT(ed.id) as emprunt_count
            FROM emprunt_detail ed
            JOIN emprunt e ON ed.emprunt_id = e.id
            WHERE e.statut_emprunt IN ('en_cours', 'En cours')
            GROUP BY ed.livre_id
        ) emp ON l.id = emp.livre_id
        WHERE 
            u.id = :id
    """, nativeQuery = true)
    List<ListeLivreParAdherantProjection> findAvailableLivres(@Param("id") Integer id);



        @Query(value = """
        SELECT 
            l.id AS livreId,
            l.titre AS titre,
            l.examplaire AS totalExemplaires,
            (COALESCE(historique_count, 0) + COALESCE(reservation_count, 0) + COALESCE(emprunt_count, 0)) AS exemplairesIndisponibles,
            (l.examplaire - (COALESCE(historique_count, 0) + COALESCE(reservation_count, 0) + COALESCE(emprunt_count, 0))) AS exemplairesDisponibles
        FROM 
            livre l
        LEFT JOIN (
            SELECT 
                hl.livre_id,
                COUNT(hl.id) as historique_count
            FROM historique_livre hl
            JOIN statut_livre sl ON hl.statut_id = sl.id
            WHERE sl.nom = 'en_cours_de_pret'
            GROUP BY hl.livre_id
        ) h ON l.id = h.livre_id
        LEFT JOIN (
            SELECT 
                r.livre_id,
                COUNT(r.id) as reservation_count
            FROM reservation r
            WHERE r.est_validee = false
            GROUP BY r.livre_id
        ) res ON l.id = res.livre_id
        LEFT JOIN (
            SELECT 
                ed.livre_id,
                COUNT(ed.id) as emprunt_count
            FROM emprunt_detail ed
            JOIN emprunt e ON ed.emprunt_id = e.id
            WHERE e.statut_emprunt IN ('en_cours', 'En cours')
            GROUP BY ed.livre_id
        ) emp ON l.id = emp.livre_id
        WHERE 
            l.id = :idlivre
    """, nativeQuery = true)
    LivreDisponibiliteProjection findLivreDisponibiliteById(@Param("idlivre") Integer idlivre);

    @Query(value = """
        SELECT 
            l.id AS livreId,
            l.titre AS titre,
            l.examplaire AS totalExemplaires,
            (COALESCE(historique_count, 0) + COALESCE(reservation_count, 0) + COALESCE(emprunt_count, 0)) AS exemplairesIndisponibles,
            (l.examplaire - (COALESCE(historique_count, 0) + COALESCE(reservation_count, 0) + COALESCE(emprunt_count, 0))) AS exemplairesDisponibles
        FROM 
            livre l
        LEFT JOIN (
            SELECT 
                hl.livre_id,
                COUNT(hl.id) as historique_count
            FROM historique_livre hl
            JOIN statut_livre sl ON hl.statut_id = sl.id
            WHERE sl.nom = 'en_cours_de_pret'
            GROUP BY hl.livre_id
        ) h ON l.id = h.livre_id
        LEFT JOIN (
            SELECT 
                r.livre_id,
                COUNT(r.id) as reservation_count
            FROM reservation r
            WHERE r.est_validee = false
            GROUP BY r.livre_id
        ) res ON l.id = res.livre_id
        LEFT JOIN (
            SELECT 
                ed.livre_id,
                COUNT(ed.id) as emprunt_count
            FROM emprunt_detail ed
            JOIN emprunt e ON ed.emprunt_id = e.id
            WHERE e.statut_emprunt IN ('en_cours', 'En cours')
            GROUP BY ed.livre_id
        ) emp ON l.id = emp.livre_id
        WHERE 
            l.id = :idlivre
    """, nativeQuery = true)
    LivreDisponibiliteProjection findLivreDisponibiliteReelleById(@Param("idlivre") Integer idlivre);

}