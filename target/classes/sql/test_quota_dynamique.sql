-- Script de test pour le système de quota dynamique
-- Ce script teste le nouveau système où le quota diminue à chaque emprunt et augmente à chaque retour

-- 1. Vérifier l'état initial des quotas
SELECT 
    '=== ÉTAT INITIAL DES QUOTAS ===' as info;

SELECT 
    u.id,
    u.nom,
    u.prenom,
    a.type as type_adherant,
    a.nbr_livre_pret as limite_adhérent,
    u.quota_actuel as quota_actuel,
    COUNT(e.id) as emprunts_en_cours,
    CASE 
        WHEN u.quota_actuel > 0 THEN '✅ Peut emprunter'
        ELSE '❌ Quota épuisé'
    END as statut
FROM utilisateur u
JOIN adherant a ON u.id_adherant = a.id
LEFT JOIN emprunt e ON u.id = e.utilisateur_id 
    AND e.statut_emprunt IN ('en_cours', 'En cours')
GROUP BY u.id, u.nom, u.prenom, a.type, a.nbr_livre_pret, u.quota_actuel
ORDER BY u.id;

-- 2. Simulation d'un emprunt (diminution du quota)
-- Pour l'utilisateur 1, simuler un emprunt
SELECT 
    '=== SIMULATION D\'UN EMPRUNT (utilisateur 1) ===' as info;

-- Avant l'emprunt
SELECT 
    'Avant emprunt' as moment,
    u.quota_actuel as quota_avant
FROM utilisateur u 
WHERE u.id = 1;

-- Après l'emprunt (simulation)
UPDATE utilisateur 
SET quota_actuel = quota_actuel - 1 
WHERE id = 1 AND quota_actuel > 0;

SELECT 
    'Après emprunt' as moment,
    u.quota_actuel as quota_apres
FROM utilisateur u 
WHERE u.id = 1;

-- 3. Simulation d'un retour (augmentation du quota)
SELECT 
    '=== SIMULATION D\'UN RETOUR (utilisateur 1) ===' as info;

-- Avant le retour
SELECT 
    'Avant retour' as moment,
    u.quota_actuel as quota_avant
FROM utilisateur u 
WHERE u.id = 1;

-- Après le retour (simulation)
UPDATE utilisateur 
SET quota_actuel = LEAST(quota_actuel + 1, (
    SELECT a.nbr_livre_pret 
    FROM adherant a 
    JOIN utilisateur u2 ON a.id = u2.id_adherant 
    WHERE u2.id = utilisateur.id
))
WHERE id = 1;

SELECT 
    'Après retour' as moment,
    u.quota_actuel as quota_apres
FROM utilisateur u 
WHERE u.id = 1;

-- 4. Vérification finale
SELECT 
    '=== ÉTAT FINAL DES QUOTAS ===' as info;

SELECT 
    u.id,
    u.nom,
    u.prenom,
    a.type as type_adherant,
    a.nbr_livre_pret as limite_adhérent,
    u.quota_actuel as quota_actuel,
    COUNT(e.id) as emprunts_en_cours,
    CASE 
        WHEN u.quota_actuel > 0 THEN '✅ Peut emprunter'
        ELSE '❌ Quota épuisé'
    END as statut
FROM utilisateur u
JOIN adherant a ON u.id_adherant = a.id
LEFT JOIN emprunt e ON u.id = e.utilisateur_id 
    AND e.statut_emprunt IN ('en_cours', 'En cours')
GROUP BY u.id, u.nom, u.prenom, a.type, a.nbr_livre_pret, u.quota_actuel
ORDER BY u.id;

-- 5. Test de validation des règles de quota
SELECT 
    '=== VALIDATION DES RÈGLES DE QUOTA ===' as info;

-- Vérifier que le quota ne peut pas dépasser la limite
SELECT 
    u.id,
    u.nom,
    u.quota_actuel,
    a.nbr_livre_pret as limite,
    CASE 
        WHEN u.quota_actuel > a.nbr_livre_pret THEN '❌ Quota dépasse la limite'
        WHEN u.quota_actuel < 0 THEN '❌ Quota négatif'
        ELSE '✅ Quota valide'
    END as validation
FROM utilisateur u
JOIN adherant a ON u.id_adherant = a.id
ORDER BY u.id;

-- 6. Test de scénario complet
SELECT 
    '=== SCÉNARIO COMPLET ===' as info;

-- Utilisateur avec quota de 3 qui emprunte 2 livres
-- État initial: quota = 3
-- Après 1er emprunt: quota = 2
-- Après 2ème emprunt: quota = 1
-- Après 1er retour: quota = 2
-- Après 2ème retour: quota = 3

SELECT 
    'Scénario: Utilisateur avec quota de 3' as description,
    'État initial: quota = 3' as etape1,
    'Après 1er emprunt: quota = 2' as etape2,
    'Après 2ème emprunt: quota = 1' as etape3,
    'Après 1er retour: quota = 2' as etape4,
    'Après 2ème retour: quota = 3' as etape5; 