-- Script de test pour les vérifications de quota lors des prolongements
-- Ce script teste les nouvelles fonctionnalités ajoutées

-- 1. Vérification des utilisateurs et leurs quotas
SELECT '=== VÉRIFICATION DES UTILISATEURS ET QUOTAS ===' as test;
SELECT 
    u.id,
    u.nom,
    u.prenom,
    u.quota_actuel,
    a.nom as type_adherant,
    a.quota_max
FROM utilisateur u
JOIN adherant a ON u.id_adherant = a.id
ORDER BY u.id;

-- 2. Vérification des demandes de prolongement existantes
SELECT '=== DEMANDES DE PROLONGEMENT EXISTANTES ===' as test;
SELECT 
    dp.id,
    u.nom,
    u.prenom,
    u.quota_actuel,
    dp.statut,
    dp.date_demande,
    dp.date_prolongement_demandee,
    dp.motif
FROM demande_prolongement dp
JOIN utilisateur u ON dp.utilisateur_id = u.id
ORDER BY dp.id;

-- 3. Test de création d'une demande avec quota insuffisant
-- Créer un utilisateur avec quota 0 pour tester
INSERT INTO utilisateur (id, nom, prenom, email, mdp, date_naissance, est_admin, id_adherant, quota_actuel) 
VALUES (6, 'Test', 'QuotaZero', 'test.quota@itu.mg', 'test123', '1995-01-01', false, 1, 0);

-- Créer un emprunt pour cet utilisateur
INSERT INTO emprunt (id, utilisateur_id, date_emprunt, date_retour, statut_emprunt) 
VALUES (6, 6, '2025-07-10', '2025-07-24', 'en_cours');

INSERT INTO emprunt_detail (id, emprunt_id, livre_id, date_debut, date_fin, date_retour) 
VALUES (6, 6, 1, '2025-07-10', '2025-07-24', NULL);

-- 4. Vérification des conditions de prolongement pour chaque utilisateur
SELECT '=== CONDITIONS DE PROLONGEMENT PAR UTILISATEUR ===' as test;
SELECT 
    u.id,
    u.nom,
    u.prenom,
    u.quota_actuel,
    COUNT(CASE WHEN dp.statut = 'en_attente' THEN 1 END) as demandes_en_attente,
    CASE 
        WHEN u.quota_actuel > 0 AND COUNT(CASE WHEN dp.statut = 'en_attente' THEN 1 END) < 3 
        THEN '✅ Peut prolonger'
        ELSE '❌ Ne peut pas prolonger'
    END as peut_prolonger,
    CASE 
        WHEN u.quota_actuel <= 0 THEN 'Quota insuffisant'
        WHEN COUNT(CASE WHEN dp.statut = 'en_attente' THEN 1 END) >= 3 THEN 'Trop de demandes en attente'
        ELSE 'OK'
    END as raison
FROM utilisateur u
LEFT JOIN demande_prolongement dp ON u.id = dp.utilisateur_id
GROUP BY u.id, u.nom, u.prenom, u.quota_actuel
ORDER BY u.id;

-- 5. Test de simulation d'approbation avec quota insuffisant
SELECT '=== SIMULATION D''APPROBATION AVEC QUOTA INSUFFISANT ===' as test;
SELECT 
    dp.id as demande_id,
    u.nom,
    u.prenom,
    u.quota_actuel,
    CASE 
        WHEN u.quota_actuel > 0 THEN '✅ Peut être approuvée'
        ELSE '❌ Ne peut pas être approuvée (quota insuffisant)'
    END as peut_etre_approuvee
FROM demande_prolongement dp
JOIN utilisateur u ON dp.utilisateur_id = u.id
WHERE dp.statut = 'en_attente'
ORDER BY dp.id;

-- 6. Statistiques des demandes par statut
SELECT '=== STATISTIQUES DES DEMANDES ===' as test;
SELECT 
    statut,
    COUNT(*) as nombre,
    CASE 
        WHEN statut = 'en_attente' THEN 'En attente d''approbation'
        WHEN statut = 'acceptee' THEN 'Approuvées'
        WHEN statut = 'refusee' THEN 'Refusées'
        ELSE 'Autre'
    END as description
FROM demande_prolongement
GROUP BY statut
ORDER BY statut;

-- 7. Vérification des emprunts et leurs statuts
SELECT '=== EMPRUNTS ET STATUTS ===' as test;
SELECT 
    e.id,
    u.nom,
    u.prenom,
    e.date_emprunt,
    e.date_retour,
    e.statut_emprunt,
    l.titre as livre
FROM emprunt e
JOIN utilisateur u ON e.utilisateur_id = u.id
JOIN emprunt_detail ed ON e.id = ed.emprunt_id
JOIN livre l ON ed.livre_id = l.id
ORDER BY e.id;

-- 8. Test de nettoyage (optionnel - décommentez pour nettoyer les données de test)
-- DELETE FROM emprunt_detail WHERE emprunt_id = 6;
-- DELETE FROM emprunt WHERE id = 6;
-- DELETE FROM utilisateur WHERE id = 6;

SELECT '=== FIN DES TESTS ===' as test; 