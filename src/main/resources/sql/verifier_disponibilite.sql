-- Script pour vérifier la disponibilité des livres
-- Ce script aide à diagnostiquer pourquoi la disponibilité n'est pas correcte

-- 1. Vérifier les livres et leurs exemplaires totaux
SELECT 
    l.id,
    l.titre,
    l.examplaire as total_exemplaires
FROM livre l
ORDER BY l.id;

-- 2. Vérifier les emprunts en cours
SELECT 
    e.id as emprunt_id,
    e.statut_emprunt,
    e.date_emprunt,
    e.date_retour,
    ed.livre_id,
    l.titre,
    u.nom,
    u.prenom
FROM emprunt e
JOIN emprunt_detail ed ON e.id = ed.emprunt_id
JOIN livre l ON ed.livre_id = l.id
JOIN utilisateur u ON e.utilisateur_id = u.id
WHERE e.statut_emprunt IN ('en_cours', 'En cours')
ORDER BY ed.livre_id;

-- 3. Vérifier les réservations
SELECT 
    r.id as reservation_id,
    r.livre_id,
    l.titre,
    u.nom,
    u.prenom,
    r.date_reservation
FROM reservation r
JOIN livre l ON r.livre_id = l.id
JOIN utilisateur u ON r.utilisateur_id = u.id
WHERE r.est_validee = false
ORDER BY r.livre_id;

-- 4. Vérifier l'historique des livres
SELECT 
    hl.id,
    hl.livre_id,
    l.titre,
    sl.nom as statut,
    hl.date_debut,
    hl.date_fin
FROM historique_livre hl
JOIN livre l ON hl.livre_id = l.id
JOIN statut_livre sl ON hl.statut_id = sl.id
ORDER BY hl.livre_id;

-- 5. Calcul manuel de la disponibilité pour le livre "Bases de données avancées" (ID = 2)
SELECT 
    'Livre' as type,
    l.id,
    l.titre,
    l.examplaire as total
FROM livre l
WHERE l.id = 2

UNION ALL

SELECT 
    'Emprunts en cours' as type,
    ed.livre_id,
    l.titre,
    COUNT(*) as total
FROM emprunt_detail ed
JOIN emprunt e ON ed.emprunt_id = e.id
JOIN livre l ON ed.livre_id = l.id
WHERE ed.livre_id = 2 
  AND e.statut_emprunt IN ('en_cours', 'En cours')
GROUP BY ed.livre_id, l.titre

UNION ALL

SELECT 
    'Réservations' as type,
    r.livre_id,
    l.titre,
    COUNT(*) as total
FROM reservation r
JOIN livre l ON r.livre_id = l.id
WHERE r.livre_id = 2 
  AND r.est_validee = false
GROUP BY r.livre_id, l.titre

UNION ALL

SELECT 
    'Historique en cours' as type,
    hl.livre_id,
    l.titre,
    COUNT(*) as total
FROM historique_livre hl
JOIN livre l ON hl.livre_id = l.id
JOIN statut_livre sl ON hl.statut_id = sl.id
WHERE hl.livre_id = 2 
  AND sl.nom = 'en_cours_de_pret'
GROUP BY hl.livre_id, l.titre;

-- 6. Test de la nouvelle requête de disponibilité
SELECT 
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