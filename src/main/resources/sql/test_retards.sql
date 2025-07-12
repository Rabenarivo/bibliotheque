-- Script de test pour la fonctionnalité des retards
-- Ce script permet de tester et vérifier la gestion des retards

-- 1. Vérifier les statuts disponibles
SELECT '=== STATUTS DISPONIBLES ===' as info;
SELECT id, nom FROM statut_livre ORDER BY id;

-- 2. Vérifier les emprunts actuels et leur statut
SELECT '=== EMPRUNTS ACTUELS ===' as info;
SELECT 
    e.id as emprunt_id,
    u.nom || ' ' || u.prenom as utilisateur,
    l.titre as livre,
    e.date_emprunt,
    e.date_retour,
    e.statut_emprunt,
    CASE 
        WHEN e.date_retour < CURRENT_DATE AND e.statut_emprunt IN ('en_cours', 'En cours') 
        THEN CURRENT_DATE - e.date_retour 
        ELSE 0 
    END as jours_de_retard
FROM emprunt e
LEFT JOIN utilisateur u ON e.utilisateur_id = u.id
LEFT JOIN emprunt_detail ed ON ed.emprunt_id = e.id
LEFT JOIN livre l ON ed.livre_id = l.id
ORDER BY e.date_retour;

-- 3. Identifier les emprunts en retard potentiels
SELECT '=== EMPRUNTS EN RETARD POTENTIELS ===' as info;
SELECT 
    e.id as emprunt_id,
    u.nom || ' ' || u.prenom as utilisateur,
    l.titre as livre,
    e.date_emprunt,
    e.date_retour,
    e.statut_emprunt,
    CURRENT_DATE - e.date_retour as jours_de_retard
FROM emprunt e
LEFT JOIN utilisateur u ON e.utilisateur_id = u.id
LEFT JOIN emprunt_detail ed ON ed.emprunt_id = e.id
LEFT JOIN livre l ON ed.livre_id = l.id
WHERE e.date_retour < CURRENT_DATE AND e.statut_emprunt IN ('en_cours', 'En cours')
ORDER BY e.date_retour;

-- 4. Compter les emprunts par statut
SELECT '=== COMPTAGE PAR STATUT ===' as info;
SELECT 
    statut_emprunt,
    COUNT(*) as nombre_emprunts
FROM emprunt 
GROUP BY statut_emprunt
ORDER BY statut_emprunt;

-- 5. Compter les emprunts en retard
SELECT '=== COMPTAGE DES RETARDS ===' as info;
SELECT 
    COUNT(*) as emprunts_en_retard
FROM emprunt 
WHERE date_retour < CURRENT_DATE AND statut_emprunt IN ('en_cours', 'En cours');

-- 6. Vérifier les livres avec leur statut actuel
SELECT '=== STATUT DES LIVRES ===' as info;
SELECT 
    l.id,
    l.titre,
    sl.nom as statut_livre,
    l.examplaire as exemplaires
FROM livre l
LEFT JOIN statut_livre sl ON l.statut_id = sl.id
ORDER BY l.id;

-- 7. Simuler la mise à jour des retards (pour test)
-- Cette requête montre ce qui se passerait si on mettait à jour les statuts
SELECT '=== SIMULATION MISE À JOUR RETARDS ===' as info;
SELECT 
    e.id as emprunt_id,
    u.nom || ' ' || u.prenom as utilisateur,
    l.titre as livre,
    e.statut_emprunt as statut_actuel,
    CASE 
        WHEN e.date_retour < CURRENT_DATE AND e.statut_emprunt IN ('en_cours', 'En cours') 
        THEN 'retard'
        ELSE e.statut_emprunt 
    END as statut_propose
FROM emprunt e
LEFT JOIN utilisateur u ON e.utilisateur_id = u.id
LEFT JOIN emprunt_detail ed ON ed.emprunt_id = e.id
LEFT JOIN livre l ON ed.livre_id = l.id
WHERE e.date_retour < CURRENT_DATE AND e.statut_emprunt IN ('en_cours', 'En cours')
ORDER BY e.date_retour; 