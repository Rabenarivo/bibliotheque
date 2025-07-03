-- Script de test pour vérifier la disponibilité des livres
-- Ce script montre comment calculer la disponibilité réelle

-- 1. Voir tous les livres avec leur nombre total d'exemplaires
SELECT 
    l.id,
    l.titre,
    l.examplaire AS total_exemplaires
FROM livre l
ORDER BY l.id;

-- 2. Voir les livres actuellement empruntés (dans historique_livre)
SELECT 
    l.id AS livre_id,
    l.titre,
    COUNT(hl.id) AS exemplaires_empruntes
FROM livre l
LEFT JOIN historique_livre hl ON l.id = hl.livre_id
LEFT JOIN statut_livre sl ON hl.statut_id = sl.id
WHERE sl.nom = 'en_cours_de_pret'
GROUP BY l.id, l.titre
ORDER BY l.id;

-- 3. Voir les livres actuellement réservés
SELECT 
    l.id AS livre_id,
    l.titre,
    COUNT(r.id) AS exemplaires_reserves
FROM livre l
LEFT JOIN reservation r ON l.id = r.livre_id
GROUP BY l.id, l.titre
ORDER BY l.id;

-- 4. Calculer la disponibilité réelle (emprunts + réservations)
SELECT 
    l.id AS livre_id,
    l.titre,
    l.examplaire AS total_exemplaires,
    COALESCE(empruntes.count_empruntes, 0) AS exemplaires_empruntes,
    COALESCE(reservees.count_reservees, 0) AS exemplaires_reserves,
    (l.examplaire - COALESCE(empruntes.count_empruntes, 0) - COALESCE(reservees.count_reservees, 0)) AS exemplaires_disponibles
FROM livre l
LEFT JOIN (
    SELECT 
        hl.livre_id,
        COUNT(hl.id) AS count_empruntes
    FROM historique_livre hl
    JOIN statut_livre sl ON hl.statut_id = sl.id
    WHERE sl.nom = 'en_cours_de_pret'
    GROUP BY hl.livre_id
) empruntes ON l.id = empruntes.livre_id
LEFT JOIN (
    SELECT 
        r.livre_id,
        COUNT(r.id) AS count_reservees
    FROM reservation r
    GROUP BY r.livre_id
) reservees ON l.id = reservees.livre_id
ORDER BY l.id;

-- 5. Voir les détails des emprunts actifs
SELECT 
    e.id AS emprunt_id,
    u.nom AS utilisateur_nom,
    u.prenom AS utilisateur_prenom,
    l.titre AS livre_titre,
    e.date_emprunt,
    e.date_retour,
    e.statut_emprunt
FROM emprunt e
JOIN utilisateur u ON e.utilisateur_id = u.id
JOIN emprunt_detail ed ON e.id = ed.emprunt_id
JOIN livre l ON ed.livre_id = l.id
WHERE e.statut_emprunt = 'en_cours'
ORDER BY e.date_emprunt;

-- 6. Voir les détails des réservations actives
SELECT 
    r.id AS reservation_id,
    u.nom AS utilisateur_nom,
    u.prenom AS utilisateur_prenom,
    l.titre AS livre_titre,
    r.date_debut,
    r.date_fin
FROM reservation r
JOIN utilisateur u ON r.utilisateur_id = u.id
JOIN livre l ON r.livre_id = l.id
ORDER BY r.date_debut; 