-- Script de mise à jour du système de quota dynamique
-- Ce script ajoute la colonne quota_actuel à la table utilisateur et initialise les quotas

-- 1. Ajouter la colonne quota_actuel à la table utilisateur
ALTER TABLE utilisateur ADD COLUMN quota_actuel INT;

-- 2. Initialiser les quotas existants avec la limite de leur adhérent
UPDATE utilisateur 
SET quota_actuel = (
    SELECT a.nbr_livre_pret 
    FROM adherant a 
    WHERE a.id = utilisateur.id_adherant
)
WHERE id_adherant IS NOT NULL;

-- 3. Mettre à jour les quotas en fonction des emprunts en cours existants
-- Pour chaque utilisateur, diminuer le quota du nombre d'emprunts en cours
UPDATE utilisateur 
SET quota_actuel = quota_actuel - (
    SELECT COUNT(e.id) 
    FROM emprunt e 
    WHERE e.utilisateur_id = utilisateur.id 
    AND e.statut_emprunt IN ('en_cours', 'En cours')
)
WHERE quota_actuel IS NOT NULL;

-- 4. S'assurer que le quota ne soit pas négatif
UPDATE utilisateur 
SET quota_actuel = 0 
WHERE quota_actuel < 0;

-- 5. Vérification des quotas après mise à jour
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