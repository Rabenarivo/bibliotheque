-- Script de test pour la correction de disponibilité V2
-- Ce script teste la nouvelle requête corrigée qui évite les doublons

-- 1. Vérifier l'état actuel du livre "Bases de données avancées" (ID = 2)
SELECT 
    'État du livre' as info,
    l.id,
    l.titre,
    l.examplaire as total_exemplaires
FROM livre l
WHERE l.id = 2;

-- 2. Compter les emprunts en cours pour ce livre (méthode corrigée)
SELECT 
    'Emprunts en cours (corrigé)' as info,
    ed.livre_id,
    COUNT(ed.id) as emprunts_en_cours
FROM emprunt_detail ed
JOIN emprunt e ON ed.emprunt_id = e.id
WHERE ed.livre_id = 2 
  AND e.statut_emprunt IN ('en_cours', 'En cours')
GROUP BY ed.livre_id;

-- 3. Compter les réservations pour ce livre
SELECT 
    'Réservations' as info,
    r.livre_id,
    COUNT(r.id) as reservations
FROM reservation r
WHERE r.livre_id = 2 
  AND r.est_validee = false
GROUP BY r.livre_id;

-- 4. Compter l'historique en cours pour ce livre
SELECT 
    'Historique en cours' as info,
    hl.livre_id,
    COUNT(hl.id) as historique_en_cours
FROM historique_livre hl
JOIN statut_livre sl ON hl.statut_id = sl.id
WHERE hl.livre_id = 2 
  AND sl.nom = 'en_cours_de_pret'
GROUP BY hl.livre_id;

-- 5. Test de la nouvelle requête corrigée
SELECT 
    'Test de la requête corrigée' as test,
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
    l.id = 2;

-- 6. Comparaison avec l'ancienne requête (pour voir le problème)
SELECT 
    'Ancienne requête (problématique)' as test,
    l.id AS livreId,
    l.titre AS titre,
    l.examplaire AS totalExemplaires,
    (COUNT(CASE WHEN sl.nom = 'en_cours_de_pret' THEN hl.id END) + 
     COUNT(CASE WHEN r.id IS NOT NULL THEN r.id END) +
     COUNT(CASE WHEN e.statut_emprunt IN ('en_cours', 'En cours') THEN ed.id END)) AS exemplairesIndisponibles,
    (l.examplaire - (COUNT(CASE WHEN sl.nom = 'en_cours_de_pret' THEN hl.id END) + 
                    COUNT(CASE WHEN r.id IS NOT NULL THEN r.id END) +
                    COUNT(CASE WHEN e.statut_emprunt IN ('en_cours', 'En cours') THEN ed.id END))) AS exemplairesDisponibles
FROM 
    livre l
LEFT JOIN 
    historique_livre hl ON l.id = hl.livre_id
LEFT JOIN 
    statut_livre sl ON hl.statut_id = sl.id
LEFT JOIN 
    reservation r ON l.id = r.livre_id
LEFT JOIN 
    emprunt_detail ed ON l.id = ed.livre_id
LEFT JOIN 
    emprunt e ON ed.emprunt_id = e.id
WHERE 
    l.id = 2
GROUP BY 
    l.id, l.titre, l.examplaire;

-- 7. Détail des emprunts pour comprendre le problème
SELECT 
    'Détail des emprunts' as info,
    e.id as emprunt_id,
    e.statut_emprunt,
    ed.livre_id,
    l.titre,
    u.nom,
    u.prenom
FROM emprunt e
JOIN emprunt_detail ed ON e.id = ed.emprunt_id
JOIN livre l ON ed.livre_id = l.id
JOIN utilisateur u ON e.utilisateur_id = u.id
WHERE ed.livre_id = 2 
  AND e.statut_emprunt IN ('en_cours', 'En cours')
ORDER BY e.id;
