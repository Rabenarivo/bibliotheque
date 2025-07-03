-- Script de test pour la disponibilité côté client
-- Ce script teste la requête findAvailableLivres corrigée

-- 1. Vérifier l'utilisateur et son adhérent
SELECT 
    'Utilisateur et adhérent' as info,
    u.id as user_id,
    u.nom,
    u.prenom,
    a.id as adherant_id,
    a.nom as adherant_nom
FROM utilisateur u
JOIN adherant a ON u.id_adherant = a.id
WHERE u.id = 1;

-- 2. Vérifier les livres accessibles à cet adhérent
SELECT 
    'Livres accessibles à l\'adhérent' as info,
    l.id,
    l.titre,
    l.examplaire as total_exemplaires
FROM livre l
JOIN livre_adherant la ON l.id = la.livre_id
JOIN adherant a ON la.adherant_id = a.id
JOIN utilisateur u ON u.id_adherant = a.id
WHERE u.id = 1;

-- 3. Test de la nouvelle requête findAvailableLivres (corrigée)
SELECT 
    'Test findAvailableLivres (corrigée)' as test,
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
    u.id = 1;

-- 4. Comparaison avec l'ancienne requête (pour voir le problème)
SELECT 
    'Ancienne requête (problématique)' as test,
    l.id AS id,
    l.titre AS titre,
    l.auteur AS auteur,
    l.age AS age,
    l.image AS image
FROM livre l
JOIN livre_adherant la ON l.id = la.livre_id
JOIN adherant a ON la.adherant_id = a.id
JOIN utilisateur u ON u.id_adherant = a.id
WHERE u.id = 1;

-- 5. Détail des emprunts pour le livre "Bases de données avancées" (ID = 2)
SELECT 
    'Détail des emprunts pour livre ID=2' as info,
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

-- 6. Vérifier les réservations pour le livre ID=2
SELECT 
    'Réservations pour livre ID=2' as info,
    r.id,
    r.livre_id,
    r.est_validee,
    u.nom,
    u.prenom
FROM reservation r
JOIN utilisateur u ON r.utilisateur_id = u.id
WHERE r.livre_id = 2;

-- 7. Vérifier l'historique pour le livre ID=2
SELECT 
    'Historique pour livre ID=2' as info,
    hl.id,
    hl.livre_id,
    sl.nom as statut_nom,
    hl.date_action
FROM historique_livre hl
JOIN statut_livre sl ON hl.statut_id = sl.id
WHERE hl.livre_id = 2; 