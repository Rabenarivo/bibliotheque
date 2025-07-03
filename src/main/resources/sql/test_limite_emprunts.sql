-- Script de test pour la vérification de limite d'emprunts
-- Ce script teste la nouvelle fonctionnalité de vérification du nombre maximum d'emprunts

-- 1. Vérifier les types d'adhérents et leurs limites
SELECT 
    a.id,
    a.type,
    a.nbr_livre_pret as limite_emprunts,
    a.nbr_reservation as limite_reservations,
    a.nbr_jrs_pret as duree_emprunt
FROM adherant a
ORDER BY a.id;

-- 2. Vérifier les utilisateurs et leurs adhérents
SELECT 
    u.id as user_id,
    u.nom,
    u.prenom,
    u.email,
    a.id as adherant_id,
    a.type,
    a.nbr_livre_pret as limite_emprunts
FROM utilisateur u
JOIN adherant a ON u.id_adherant = a.id
ORDER BY u.id;

-- 3. Compter les emprunts en cours par utilisateur
SELECT 
    u.id as user_id,
    u.nom,
    u.prenom,
    a.nbr_livre_pret as limite_emprunts,
    COUNT(e.id) as emprunts_en_cours
FROM utilisateur u
JOIN adherant a ON u.id_adherant = a.id
LEFT JOIN emprunt e ON u.id = e.utilisateur_id 
    AND e.statut_emprunt IN ('en_cours', 'En cours')
GROUP BY u.id, u.nom, u.prenom, a.nbr_livre_pret
ORDER BY u.id;

-- 4. Vérifier si les utilisateurs peuvent emprunter plus
SELECT 
    u.id as user_id,
    u.nom,
    u.prenom,
    a.nbr_livre_pret as limite_emprunts,
    COUNT(e.id) as emprunts_en_cours,
    (a.nbr_livre_pret - COUNT(e.id)) as emprunts_restants,
    CASE 
        WHEN COUNT(e.id) >= a.nbr_livre_pret THEN '❌ Limite atteinte'
        WHEN (a.nbr_livre_pret - COUNT(e.id)) <= 2 THEN '⚠️ Peu d\'emprunts restants'
        ELSE '✅ Peut emprunter'
    END as statut
FROM utilisateur u
JOIN adherant a ON u.id_adherant = a.id
LEFT JOIN emprunt e ON u.id = e.utilisateur_id 
    AND e.statut_emprunt IN ('en_cours', 'En cours')
GROUP BY u.id, u.nom, u.prenom, a.nbr_livre_pret
ORDER BY u.id;

-- 5. Détail des emprunts en cours pour un utilisateur spécifique (exemple: utilisateur 1)
SELECT 
    'Détail des emprunts en cours' as info,
    e.id as emprunt_id,
    e.statut_emprunt,
    e.date_emprunt,
    e.date_retour,
    l.titre,
    l.auteur
FROM emprunt e
JOIN emprunt_detail ed ON e.id = ed.emprunt_id
JOIN livre l ON ed.livre_id = l.id
WHERE e.utilisateur_id = 1 
  AND e.statut_emprunt IN ('en_cours', 'En cours')
ORDER BY e.date_emprunt;

-- 6. Test de la requête de comptage (simulation de la méthode countEmpruntsEnCours)
SELECT 
    'Test de comptage pour utilisateur 1' as test,
    COUNT(e.id) as emprunts_en_cours
FROM emprunt e
WHERE e.utilisateur_id = 1 
  AND e.statut_emprunt IN ('en_cours', 'En cours');

-- 7. Vérification complète pour tous les utilisateurs
SELECT 
    u.id,
    u.nom,
    u.prenom,
    a.type as type_adherant,
    a.nbr_livre_pret as limite,
    COUNT(e.id) as emprunts_actuels,
    (a.nbr_livre_pret - COUNT(e.id)) as restants,
    CASE 
        WHEN COUNT(e.id) >= a.nbr_livre_pret THEN 'NON'
        ELSE 'OUI'
    END as peut_emprunter
FROM utilisateur u
JOIN adherant a ON u.id_adherant = a.id
LEFT JOIN emprunt e ON u.id = e.utilisateur_id 
    AND e.statut_emprunt IN ('en_cours', 'En cours')
GROUP BY u.id, u.nom, u.prenom, a.type, a.nbr_livre_pret
ORDER BY u.id; 